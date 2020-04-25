package io.pivotal.pal.tracker;

import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    public Map<Long,TimeEntry> timeEntryRepo;
    long idCounter;

    public InMemoryTimeEntryRepository(){
        timeEntryRepo = new HashMap<>();
        idCounter = 0L;
    }

    @Override
    public TimeEntry create(TimeEntry timeEntry) {
        //long id = (UUID.randomUUID().getMostSignificantBits()& Long.MAX_VALUE);
        //long id = timeEntryRepo.size()+1;
        long id = ++idCounter;
        if(timeEntry.getId() == 0L){
            timeEntry.setId(id);
        }
        timeEntryRepo.put(id,timeEntry);
        printSize();
        return timeEntry;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry existingEntry = find(id);
        TimeEntry updatedEntry = null;
        if(existingEntry != null) {
            timeEntry.setId(id);
            timeEntryRepo.put(id, timeEntry);
            updatedEntry = timeEntry;
        }
        return updatedEntry;
    }

    @Override
    public TimeEntry find(long id) {
        return timeEntryRepo.get(id);
    }

    @Override
    public List<TimeEntry> list() {
        printSize();
        return timeEntryRepo.values().stream().collect(Collectors.toList());
    }

    @Override
    public TimeEntry delete(long id) {
        return timeEntryRepo.remove(id);
    }

    private void printSize(String message){
        System.out.print(message);
        printSize();
    }

    private void printSize(){
        System.out.println("Items in repo: "+timeEntryRepo.size());
    }
}
