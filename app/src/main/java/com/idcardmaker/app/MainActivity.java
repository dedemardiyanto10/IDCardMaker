package com.idcardmaker.app;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.idcardmaker.app.databinding.ActivityMainBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private static final int PICK_IMAGE_REQUEST_CODE_10 = 10;
    private static final int PICK_IMAGE_REQUEST_CODE_20 = 20;
    private Uri imageUriProfile, imageUriLogo;
    private EditText ETTitle, ETSubTitle, ETName, ETCity, ETBorn, ETNik, ETExpired;
    private AutoCompleteTextView ETGender;
    private Button BTNGenerate;
    private MaterialCardView cardViewOptions, cardViewOptionsLogo;
    private ImageView IMGProfile, IMGLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ETTitle = binding.ETTitle;
        ETSubTitle = binding.ETSubTitle;
        ETName = binding.ETName;
        ETCity = binding.ETCity;
        ETBorn = binding.ETBorn;
        ETGender = binding.ETGender;
        ETNik = binding.ETNik;
        ETExpired = binding.ETExpired;

        BTNGenerate = binding.BTNGenerate;
        cardViewOptions = binding.cardViewOptions;
        cardViewOptionsLogo = binding.cardViewOptionsLogo;
        IMGProfile = binding.IMGProfile;
        IMGLogo = binding.IMGLogo;

        String nikrandom = generateRandomNIK();
        ETNik.setText(nikrandom);

        ETGender.setText("Laki-laki");

        String[] simpleItems = getResources().getStringArray(R.array.gender);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, simpleItems);
        ETGender.setAdapter(adapter);

        ETBorn.setOnClickListener(
                v -> {
                    DialogEditBorn();
                });

        cardViewOptions.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE_10);
                });

        cardViewOptionsLogo.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE_20);
                });

        BTNGenerate.setOnClickListener(
                v -> {
                    if (isEditTextFilled()) {
                        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                        if (imageUriProfile != null || imageUriLogo != null) {
                            if (imageUriProfile != null) {
                                intent.putExtra("imageUriProfile", imageUriProfile.toString());
                            }
                            if (imageUriLogo != null) {
                                intent.putExtra("imageUriLogo", imageUriLogo.toString());
                            }
                        }
                        intent.putExtra("title", ETTitle.getText().toString());
                        intent.putExtra("subTitle", ETSubTitle.getText().toString());
                        intent.putExtra("name", ETName.getText().toString());
                        intent.putExtra("city", ETCity.getText().toString());
                        intent.putExtra("born", ETBorn.getText().toString());
                        intent.putExtra("gender", ETGender.getText().toString());
                        intent.putExtra("nik", ETNik.getText().toString());
                        intent.putExtra("expired", ETExpired.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(
                                        MainActivity.this,
                                        "Please fill in all fields",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE_10) {
            handleScanResultImageProfile(resultCode, data, IMGProfile);
        } else if (requestCode == PICK_IMAGE_REQUEST_CODE_20) {
            handleScanResultImageLogo(resultCode, data, IMGLogo);
        }
    }

    private void handleScanResultImageProfile(
            int resultCode, Intent data, ImageView targetImageViewProfile) {
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUriProfile = data.getData();
            targetImageViewProfile.setImageURI(imageUriProfile);
        }
    }

    private void handleScanResultImageLogo(
            int resultCode, Intent data, ImageView targetImageViewLogo) {
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUriLogo = data.getData();
            targetImageViewLogo.setImageURI(imageUriLogo);
        }
    }

    private boolean isEditTextFilled() {
        return !ETTitle.getText().toString().isEmpty()
                && !ETSubTitle.getText().toString().isEmpty()
                && !ETName.getText().toString().isEmpty()
                && !ETCity.getText().toString().isEmpty()
                && !ETBorn.getText().toString().isEmpty()
                && !ETGender.getText().toString().isEmpty()
                && !ETNik.getText().toString().isEmpty()
                && !ETExpired.getText().toString().isEmpty();
    }

    private String generateRandomNIK() {
        StringBuilder randomNIK = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 16; i++) {
            int digit = random.nextInt(10);
            randomNIK.append(digit);
        }

        return randomNIK.toString();
    }

    private void DialogEditBorn() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Atur Tanggal Lahir");

        MaterialDatePicker<Long> materialDatePicker = builder.build();

        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    Date selectedDate = new Date(selection);
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                    String formattedDate = dateFormat.format(selectedDate);

                    ETBorn.setText(formattedDate);
                });

        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER_TAG");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
