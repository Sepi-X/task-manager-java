import command.*;
import model.*;
import queue.TaskQueue;
import strategy.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Headless checks for the model, strategies, commands and queue (no GUI, no libraries).
 *
 *   javac -d out $(find src test -name "*.java")
 *   java -cp out SmokeTest
 */
public class SmokeTest {
    private static int passed = 0, total = 0;

    private static void check(String name, boolean ok) {
        total++;
        if (ok) passed++;
        System.out.println((ok ? "PASS " : "FAIL ") + name);
    }

    public static void main(String[] args) throws Exception {
        TaskManager manager = TaskManager.getInstance();
        LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

        // 1. Priority sorting: URGENT first, LOW last
        List<Task> list = new ArrayList<>(List.of(
                new Task("low", "", base, Priority.LOW),
                new Task("urgent", "", base.plusHours(2), Priority.URGENT),
                new Task("medium", "", base, Priority.MEDIUM),
                new Task("high", "", base, Priority.HIGH)));
        new HighPriorityFirstStrategy().sortTasks(list);
        check("high-priority-first order is URGENT, HIGH, MEDIUM, LOW",
                list.stream().map(Task::getTitle).toList().equals(List.of("urgent", "high", "medium", "low")));

        // 2. Deadline sorting: same time -> higher priority first
        new DeadlineFirstStrategy().sortTasks(list);
        check("deadline-first breaks ties by higher priority",
                list.get(0).getTitle().equals("high") && list.get(3).getTitle().equals("urgent"));

        // 3. Create command adds a task
        CreateTaskCommand create = new CreateTaskCommand("Meeting", "team", base, Priority.MEDIUM);
        check("create command adds the task", create.execute() && manager.taskExists(create.getCreatedTask().getId()));

        // 4. Scheduling a new task at the same hour moves it to a free hour (no duplicates)
        Task clash = new Task("Dentist", "", base.plusMinutes(30), Priority.LOW);
        ScheduleTaskCommand schedule = new ScheduleTaskCommand(clash, new AutoRescheduleStrategy());
        boolean ok = schedule.execute();
        check("conflicting new task is rescheduled to a free hour",
                ok && manager.taskExists(clash.getId()) && manager.findConflictingTasks(clash).isEmpty()
                        && manager.getTaskCount() == 2);

        // 5. Rescheduling an EXISTING task updates it instead of failing with "Task not found"
        Task existing = new Task("Gym", "", base.plusHours(5), Priority.LOW);
        manager.addTask(existing);
        existing.setDueDate(base.plusMinutes(10)); // user moves it onto the meeting slot
        int before = manager.getTaskCount();
        ok = new ScheduleTaskCommand(existing, new AutoRescheduleStrategy()).execute();
        check("existing task is moved, not duplicated",
                ok && manager.getTaskCount() == before && manager.findConflictingTasks(existing).isEmpty());

        // 6. Priority-based strategy: a LOW task loses against a MEDIUM one and moves a day
        Task lowClash = new Task("Laundry", "", base, Priority.LOW);
        new ScheduleTaskCommand(lowClash, new PriorityBasedRescheduleStrategy()).execute();
        check("priority-based strategy moves the lower-priority task to the next day",
                lowClash.getDueDate().equals(base.plusDays(1)));

        // 7. Queue processes commands and shuts down quickly
        TaskQueue queue = TaskQueue.getInstance();
        int countBefore = manager.getTaskCount();
        queue.submitCommand(new CreateTaskCommand("Queued", "", base.plusDays(3), Priority.HIGH));
        long waitUntil = System.currentTimeMillis() + 3000;
        while (manager.getTaskCount() == countBefore && System.currentTimeMillis() < waitUntil) Thread.sleep(50);
        check("worker pool executes a queued command", manager.getTaskCount() == countBefore + 1);

        long t0 = System.currentTimeMillis();
        queue.shutdown();
        long ms = System.currentTimeMillis() - t0;
        check("queue shuts down in under 3 s (was up to 60 s), took " + ms + " ms", ms < 3000);

        System.out.println("\n" + passed + "/" + total + " tests passed");
        System.exit(passed == total ? 0 : 1);
    }
}
