package io.pivotal.pal.tracker;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;

@RestController
public class TimeEntryController {

    TimeEntryRepository timeEntryRepository;
    private final DistributionSummary timeEntrySummary;
    private final Counter actionCounter;

    public TimeEntryController(TimeEntryRepository timeEntryRepository,
                               MeterRegistry meterRegistry){
        this.timeEntryRepository = timeEntryRepository;

        timeEntrySummary = meterRegistry.summary("timeEntry.summary");
        actionCounter = meterRegistry.counter("timeEntry.actionCounter");
    }

    @PostMapping("/time-entries")
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry){
        TimeEntry createdTimeEntry = timeEntryRepository.create(timeEntry);
        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());

        return new ResponseEntity(createdTimeEntry, HttpStatus.CREATED);
    }

    @RequestMapping(value="/time-entries/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<TimeEntry> update(@PathVariable("id") long id,
                                            @RequestBody TimeEntry timeEntry){
        //TimeEntry existingTimeEntry = timeEntryRepository.find(id);
        //TimeEntry updatedTimeEntry = null;
        HttpStatus status;

        TimeEntry updatedTimeEntry = timeEntryRepository.update(id, timeEntry);
        if(updatedTimeEntry != null) {
            actionCounter.increment();
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity(updatedTimeEntry, status);
    }

    @GetMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable("id") long id){
        TimeEntry readTimeEntry = timeEntryRepository.find(id);
        HttpStatus status;
        if(readTimeEntry != null){
            actionCounter.increment();
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity(readTimeEntry, status);
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list(){
        actionCounter.increment();
        return new ResponseEntity(timeEntryRepository.list(),HttpStatus.OK);
    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable("id") long id){
        TimeEntry deletedTimeEntry = timeEntryRepository.delete(id);

        actionCounter.increment();
        timeEntrySummary.record(timeEntryRepository.list().size());

        return new ResponseEntity(deletedTimeEntry, HttpStatus.NO_CONTENT);
    }
}
