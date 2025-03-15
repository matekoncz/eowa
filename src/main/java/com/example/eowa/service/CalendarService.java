package com.example.eowa.service;

import com.example.eowa.exceptions.CalendarExceptions.CalendarException;
import com.example.eowa.exceptions.CalendarExceptions.TimeTravelException;
import com.example.eowa.exceptions.CalendarExceptions.WrongIntervalException;
import com.example.eowa.model.*;
import com.example.eowa.model.Calendar;
import com.example.eowa.repository.CalendarRepository;
import com.example.eowa.repository.DayRepository;
import com.example.eowa.repository.HourRepository;
import com.example.eowa.repository.OpinionRepository;
import jakarta.persistence.Entity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final DayRepository dayRepository;
    private final HourRepository hourRepository;
    private final OpinionRepository opinionRepository;

    public CalendarService(CalendarRepository calendarRepository, DayRepository dayRepository, HourRepository hourRepository, OpinionRepository opinionRepository) {
        this.calendarRepository = calendarRepository;
        this.dayRepository = dayRepository;
        this.hourRepository = hourRepository;
        this.opinionRepository = opinionRepository;
    }

    public void deleteAllCalendarData() {
        this.hourRepository.deleteAll();
        this.dayRepository.deleteAll();
        this.calendarRepository.deleteAll();
        this.opinionRepository.deleteAll();
    }

    public Calendar createCalendar(String zoneIdString, LocalDateTime startTime, LocalDateTime endTime) throws CalendarException {
        ZoneId zoneId = ZoneId.of(zoneIdString);
        ZonedDateTime zonedStart = ZonedDateTime.of(startTime.withHour(0).withMinute(0).withSecond(0).withNano(0), zoneId);
        ZonedDateTime zonedEnd = ZonedDateTime.of(endTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0), zoneId);

        if (zonedStart.isBefore(ZonedDateTime.now(ZoneId.of(zoneIdString)).withHour(0).withMinute(0).withSecond(0).withNano(0))
                || zonedStart.plusDays(60).isBefore(zonedEnd)) {
            throw new TimeTravelException();
        }
        if (zonedEnd.isBefore(zonedStart)) {
            throw new WrongIntervalException();
        }

        Calendar calendar = new Calendar(zoneId, zonedStart, zonedEnd);

        calendar.setDays(generateDays(calendar));
        calendarRepository.save(calendar);

        return calendar;
    }

    private List<Day> generateDays(Calendar calendar) {
        List<Day> days = new ArrayList<>();
        List<Hour> dayHours = new ArrayList<>();
        ZonedDateTime currentDay = calendar.getStartTime();
        int serial = 0;
        int hourSerial = 0;
        Day day = null;
        for (ZonedDateTime currentHour = calendar.getStartTime(); currentHour.isBefore(calendar.getEndTime().plusDays(1)); currentHour = currentHour.plusHours(1)) {
            if (currentHour.getDayOfYear() != currentDay.getDayOfYear()) {
                if(day != null){
                    if(day.getHours().size() != 24){
                        day.setHourNumberAffectedByClockChange(getHourNumberAffected(day));
                    }
                }

                day = new Day(currentDay, serial, true, false, dayHours);
                days.add(day);
                dayRepository.save(day);
                currentDay = currentHour;
                dayHours = new ArrayList<>();
                serial++;
            }
            Hour currentHourInHourFormat = new Hour(currentHour.getHour(), hourSerial, true);
            dayHours.add(currentHourInHourFormat);
            hourSerial++;
            hourRepository.save(currentHourInHourFormat);
        }
        return days;
    }

    private int getHourNumberAffected(Day day) {
        ZonedDateTime hourTime = day.getDayStartTime();
        while(hourTime.isBefore(day.getDayStartTime().plusDays(1))){
            ZonedDateTime nextHour = hourTime.plusHours(1);
            int difference = nextHour.getHour() - hourTime.getHour();
            if(difference == 0){
                return hourTime.getHour();
            }
            if(difference == 2){
                return hourTime.getHour() + 1;
            }
            hourTime = hourTime.plusHours(1);
        }
        return -1;
    }

    public Calendar getCalendarById(long id){
        return calendarRepository.findById(id).orElse(null);
    }

    private Hour getHourById(long id){
        return hourRepository.findById(id).orElse(null);
    }

    public void setUnavailableDays(Calendar calendar, Set<Integer> seralNumbers) {
        calendar.getDays().forEach(d -> d.setEnabled(!seralNumbers.contains(d.getSerialNumber())));
    }

    public void setUnavailableHours(Calendar calendar, Set<Integer> serialNumbers) {
        calendar.getDays().forEach(
                day -> day.getHours().forEach(
                        hour -> hour.setEnabled(!serialNumbers.contains(hour.getNumberInTotal()))
                )
        );
    }

    //TODO: clean up this mess

    public void setUnavailableHoursPeriodically(Calendar calendar, Set<Integer> hourNumbers, int period) {
        calendar.getDays().forEach(day -> {
            if (day.getSerialNumber() % (float) period == 0) {
                day.getHours().forEach(hour -> {
                    if (hourNumbers.contains(hour.getNumber())) {
                        hour.setEnabled(false);
                    }
                });
            }
        });
    }

    public Opinion getUserOpinion(Hour hour, User user) {
        return hour.getOpinions().stream().filter(opinion -> opinion.getUser().equals(user)).findFirst().orElse(null);
    }

    public void setUserOpinion(Calendar calendar, User user, Set<Integer> hourNumbersInTotal, Opinion.UserOpinion userOpinion) {
        calendar.getDays().forEach(day -> {
            day.getHours().forEach(hour -> {
                if (hourNumbersInTotal.contains(hour.getNumberInTotal())) {
                    Opinion existingOpinion = getUserOpinion(hour, user);
                    if (existingOpinion != null) {
                        existingOpinion.setUserOpinion(userOpinion);
                    } else {
                        Opinion opinion = new Opinion(user, userOpinion);
                        opinionRepository.save(opinion);
                        hour.getOpinions().add(opinion);
                    }
                }
            });
        });
    }

    public void removeUserOpinion(Calendar calendar, User user, Set<Integer> hourNumbersInTotal) {
        calendar.getDays().forEach(day -> {
            day.getHours().forEach(hour -> {
                if (hourNumbersInTotal.contains(hour.getNumberInTotal())) {
                    Opinion existingOpinion = getUserOpinion(hour, user);
                    if (existingOpinion != null) {
                        hour.getOpinions().remove(existingOpinion);
                        opinionRepository.delete(existingOpinion);
                    }
                }
            });
        });
    }

    public void setTimeInterval(Calendar calendar, long startHourSerial, long endHourSerial){
        if(startHourSerial>=endHourSerial){
            return;
        }
        long hourNumber = calendar.getDays().stream().map(Day::getHours).mapToLong(Collection::size).sum();
        if(hourNumber == 0 || hourNumber < endHourSerial){
            return;
        }

        calendar.setStarthour(startHourSerial);
        calendar.setEndhour(endHourSerial);
        calendarRepository.save(calendar);
    }

    public void resetTimeInterval(Calendar calendar){
        calendar.setStarthour(-1);
        calendar.setEndhour(-1);
        calendarRepository.save(calendar);
    }

    public List<MomentDetails> getBestTimeIntervals(Calendar calendar, int minParticipants, int minLength, Set<Opinion.UserOpinion> allowedOpinions){
        List<Hour> everyHour = new ArrayList<>(calendar.getDays().stream().map(Day::getHours).flatMap(Collection::stream).toList());
        everyHour.sort(Comparator.comparingInt(Hour::getNumberInTotal));

        List<MomentDetails> momentDetails = new ArrayList<>();

        everyHour.forEach(hour->{
            Set<User> sharedParticipants = getPositiveParticipants(hour,allowedOpinions);
            if(sharedParticipants.size()<minParticipants){
                return;
            }

            int nextNumber = hour.getNumberInTotal()+1;
            Hour nextHour = getHourByNumber(everyHour,nextNumber);
            int participantNumber = 0;
            while(nextHour != null){
                 participantNumber = sharedParticipants.size();
                updateSharedParticipants(sharedParticipants,getPositiveParticipants(nextHour,allowedOpinions));
                if(sharedParticipants.size()<minParticipants){
                    break;
                }
                nextNumber++;
                nextHour=getHourByNumber(everyHour,nextNumber);
            }
            momentDetails.add(new MomentDetails(hour.getId(),nextNumber-hour.getNumberInTotal(),participantNumber));
        });

        return momentDetails.stream().filter(detail->detail.getLength()>=minLength).collect(Collectors.toList());
    }

    private void updateSharedParticipants(Set<User> sharedParticipants, Set<User> positiveParticipants) {
        Set<User> participantsToRemove = new HashSet<>();
        sharedParticipants.forEach(
                user -> {
                    if(!positiveParticipants.contains(user)){
                        participantsToRemove.add(user);
                    }
                }
        );
        sharedParticipants.removeAll(participantsToRemove);
    }

    private Set<User> getPositiveParticipants(Hour hour, Set<Opinion.UserOpinion> allowedOpinions) {
        return hour.getOpinions().stream().filter(o->allowedOpinions.contains(o.getUserOpinion())).map(Opinion::getUser).collect(Collectors.toSet());
    }

    private static Hour getHourByNumber(List<Hour> everyHour, long nextNumber) {
        return everyHour.stream().filter(h -> h.getNumberInTotal() == nextNumber).findFirst().orElse(null);
    }

    public static class Period {
        public static final int DAILY = 1;
        public static final int WEEKLY = 7;
    }
}
