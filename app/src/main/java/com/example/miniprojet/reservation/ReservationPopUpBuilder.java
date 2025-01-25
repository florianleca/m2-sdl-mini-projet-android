package com.example.miniprojet.reservation;

import static lombok.AccessLevel.PRIVATE;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.miniprojet.R;
import com.example.miniprojet.restaurant.Restaurant;

import java.util.Locale;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class ReservationPopUpBuilder {

    private static final String[] displayedHours = {"12", "13", "19", "20", "21"};
    private static final String[] displayedMinutes = {"00", "15", "30", "45"};

    public static void initReservationPopUp(Activity activity, Restaurant restaurant) {
        Button bookButton = activity.findViewById(R.id.bookButton);
        bookButton.setOnClickListener(v -> {

            View popUpView = activity.getLayoutInflater().inflate(R.layout.popup_reservation, null);

            NumberPicker hourPicker = popUpView.findViewById(R.id.hourPicker);
            hourPicker.setWrapSelectorWheel(false);
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(displayedHours.length - 1);
            hourPicker.setDisplayedValues(displayedHours);

            NumberPicker minutePicker = popUpView.findViewById(R.id.minutePicker);
            minutePicker.setWrapSelectorWheel(false);
            minutePicker.setMinValue(0);
            minutePicker.setMaxValue(displayedMinutes.length - 1);
            minutePicker.setDisplayedValues(displayedMinutes);

            NumberPicker numberOfPeopleNumberPicker = popUpView.findViewById(R.id.numberOfPeople);
            numberOfPeopleNumberPicker.setMinValue(1);
            numberOfPeopleNumberPicker.setMaxValue(20);
            numberOfPeopleNumberPicker.setWrapSelectorWheel(false);

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(popUpView)
                    .setTitle("Nouvelle réservation - " + restaurant.getName())
                    .setPositiveButton("Confirmer la réservation", (dialog, which) -> initConfirmationPopUp(activity, restaurant, popUpView))
                    .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });
    }

    private static void initConfirmationPopUp(Activity activity, Restaurant restaurant, View reservationView) {
        DatePicker datePicker = reservationView.findViewById(R.id.datePicker);
        NumberPicker hourPicker = reservationView.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = reservationView.findViewById(R.id.minutePicker);
        NumberPicker numberOfPeopleNumberPicker = reservationView.findViewById(R.id.numberOfPeople);
        EditText nameEditText = reservationView.findViewById(R.id.name);
        EditText emailEditText = reservationView.findViewById(R.id.email);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        String hour = displayedHours[hourPicker.getValue()];
        String minute = displayedMinutes[minutePicker.getValue()];
        int numberOfPeople = numberOfPeopleNumberPicker.getValue();
        String plurial = numberOfPeople > 1 ? "s" : "";
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        String confirmationSummary = String.format(Locale.getDefault(), "Ta demande de réservation pour %s personne%s le %02d/%02d/%02d à %sh%s a bien été transmise à %s",
                numberOfPeople,
                plurial,
                day,
                month,
                year,
                hour,
                minute,
                restaurant.getName());
        String confirmationEmail = String.format("Un email te sera envoyé à %s lorsque le restaurant aura traité ta demande", email);

        View confirmationView = activity.getLayoutInflater().inflate(R.layout.popup_confirmation_reservation, null);
        TextView summaryTV = confirmationView.findViewById(R.id.summary);
        TextView emailTV = confirmationView.findViewById(R.id.email);
        summaryTV.setText(confirmationSummary);
        emailTV.setText(confirmationEmail);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(confirmationView)
                .setTitle(String.format("Merci %s !", name))
                .setPositiveButton("Continuer", (dialog, which) -> {
                })
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}
