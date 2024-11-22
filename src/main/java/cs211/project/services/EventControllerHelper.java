package cs211.project.services;

import cs211.project.models.Event;
import cs211.project.models.TeamAvailability;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EventControllerHelper {
    public void setBasicData(Event event,
                             Label eventNameLabel,
                             Label startDateLabel,
                             Label endDateLabel,
                             Label participantStartDateLabel,
                             Label participantEndDateLabel,
                             Label startTimeLabel,
                             Label endTimeLabel,
                             Label participantStartTimeLabel,
                             Label participantEndTimeLabel,
                             Label eventVisibilityLabel,
                             ImageView imageBanner,
                             Label teamAvailabilityLabel,
                             Label joinedParticipantLabel,
                             Label maxParticipantsLabel,
                             boolean showEventStatus
                             ) {
        eventNameLabel.setText(event.getEventName());
        startDateLabel.setText(CalculationUtility.formatDate(event.getStartDateTime()));
        endDateLabel.setText(CalculationUtility.formatDate(event.getEndDateTime()));
        participantStartDateLabel.setText(CalculationUtility.formatDate(event.getParticipantAcceptStartDateTime()));
        participantEndDateLabel.setText(CalculationUtility.formatDate(event.getParticipantAcceptEndDateTime()));

        startTimeLabel.setText(CalculationUtility.formatTime(event.getStartDateTime(),false));
        endTimeLabel.setText(CalculationUtility.formatTime(event.getEndDateTime(),false));
        participantStartTimeLabel.setText(CalculationUtility.formatTime(event.getParticipantAcceptStartDateTime(),false));
        participantEndTimeLabel.setText(CalculationUtility.formatTime(event.getParticipantAcceptEndDateTime(),false));
        startTimeLabel.setText(CalculationUtility.formatTime(event.getStartDateTime(), false));
        endTimeLabel.setText(CalculationUtility.formatTime(event.getEndDateTime(), false));
        participantStartTimeLabel.setText(CalculationUtility.formatTime(event.getParticipantAcceptStartDateTime(), false));
        participantEndTimeLabel.setText(CalculationUtility.formatTime(event.getParticipantAcceptEndDateTime(), false));

        if(showEventStatus) {
            eventVisibilityLabel.setText(event.getEventVisibility().toString()); }
        else {
            eventVisibilityLabel.setVisible(false);
        }

        Image eventImage = new Image(event.getEventBannerPath());
        imageBanner.setImage(eventImage);
        joinedParticipantLabel.setText(String.format("%d",event.getJoinedParticipants()));
        maxParticipantsLabel.setText(String.format("%d",event.getMaxParticipants()));
        teamAvailabilityLabel.setText(event.teamSeatAvailable()? TeamAvailability.AVAILABLE.toString():TeamAvailability.UNAVAILABLE.toString());
    }

}
