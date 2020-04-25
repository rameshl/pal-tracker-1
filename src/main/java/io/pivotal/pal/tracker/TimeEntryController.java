package io.pivotal.pal.tracker;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {

    TimeEntryRepository timeEntryRepository;

    public TimeEntryController(TimeEntryRepository timeEntryRepository){
        this.timeEntryRepository = timeEntryRepository;
    }

    @PostMapping("/time-entries")
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry){
        TimeEntry createdTimeEntry = timeEntryRepository.create(timeEntry);
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
            status = HttpStatus.OK;
        } else {
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity(readTimeEntry, status);
    }

    @GetMapping("/time-entries")
    public ResponseEntity<List<TimeEntry>> list(){
        return new ResponseEntity(timeEntryRepository.list(),HttpStatus.OK);
    }

    @DeleteMapping("/time-entries/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable("id") long id){
        HttpStatus status = HttpStatus.NO_CONTENT;

        TimeEntry deletedTimeEntry = timeEntryRepository.delete(id);

        /*if (deletedTimeEntry != null) {
            status = HttpStatus.NO_CONTENT;
        } else {
            status = HttpStatus.NOT_FOUND;
        }*/

        return new ResponseEntity(deletedTimeEntry, status);
    }
}
