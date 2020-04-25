package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry timeEntry);
    TimeEntry update(long id, TimeEntry timeEntry);
    TimeEntry find(long id);
    List<TimeEntry> list();
    TimeEntry delete(long id);
}
