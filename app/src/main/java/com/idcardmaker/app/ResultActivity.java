package com.idcardmaker.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.idcardmaker.app.databinding.ActivityResultBinding;
import com.makeramen.roundedimageview.RoundedImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import yuku.ambilwarna.AmbilWarnaDialog;

public class ResultActivity extends AppCompatActivity {
    private ActivityResultBinding binding;

    private static final int PICK_IMAGE_REQUEST_CODE_10 = 10;
    private TextView TXTTitle,
            TXTSubTitle,
            TXTNama,
            TXTKota,
            TXTLahir,
            TXTKelamin,
            TXTLaku,
            TXTName,
            TXTCity,
            TXTBorn,
            TXTGender,
            TXTExpired,
            TXTNik,
            TXTTitleA,
            TXTSubTitleA;
    private RoundedImageView IMGProfile, IMGLogo;
    private Button BTNPdfSave, BTNColor, BTNBackground;
    private ImageView bgBack, bgFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bgBack = binding.bgBack;
        bgFront = binding.bgFront;

        TXTTitle = binding.TXTTitle;
        TXTSubTitle = binding.TXTSubTitle;

        TXTNama = binding.TXTNama;
        TXTKota = binding.TXTKota;
        TXTLahir = binding.TXTLahir;
        TXTKelamin = binding.TXTKelamin;
        TXTLaku = binding.TXTLaku;
        TXTName = binding.TXTName;
        TXTCity = binding.TXTCity;
        TXTBorn = binding.TXTBorn;
        TXTGender = binding.TXTGender;
        TXTExpired = binding.TXTExpired;
        TXTNik = binding.TXTNik;
        TXTTitleA = binding.TXTTitleA;
        TXTSubTitleA = binding.TXTSubTitleA;
        IMGProfile = binding.IMGProfile;
        IMGLogo = binding.IMGLogo;
        BTNPdfSave = binding.BTNPdfSave;
        BTNColor = binding.BTNColor;
        BTNBackground = binding.BTNBackground;

        Typeface customFont = Typeface.createFromAsset(getAssets(), "varsity.ttf");
        TXTTitle.setTypeface(customFont);
        TXTTitleA.setTypeface(customFont);

        String imageUriProfileString = getIntent().getStringExtra("imageUriProfile");
        String imageUriLogoString = getIntent().getStringExtra("imageUriLogo");

        if (imageUriProfileString != null) {
            Uri imageUriProfile = Uri.parse(imageUriProfileString);
            IMGProfile.setImageURI(imageUriProfile);
        }

        if (imageUriLogoString != null) {
            Uri imageUriLogo = Uri.parse(imageUriLogoString);
            IMGLogo.setImageURI(imageUriLogo);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("title", "");
            String subTitle = extras.getString("subTitle", "");
            String name = extras.getString("name", "");
            String city = extras.getString("city", "");
            String born = extras.getString("born", "");
            String gender = extras.getString("gender", "");
            String nik = extras.getString("nik", "");
            String expired = extras.getString("expired", "");

            TXTTitle.setText(title);
            TXTSubTitle.setText(subTitle);
            TXTName.setText(": " + name);
            TXTCity.setText(": " + city);
            TXTBorn.setText(": " + born);
            TXTGender.setText(": " + gender);
            TXTExpired.setText(": " + expired);
            TXTNik.setText(nik);
            TXTTitleA.setText(title);
            TXTSubTitleA.setText(subTitle);

            if (!nik.isEmpty()) {
                ImageView imageViewResult = findViewById(R.id.imageViewResult);
                generateQRCode(nik, imageViewResult);
            }
        }

        BTNPdfSave.setOnClickListener(
                v -> {
                    createPdfBack();
                    createPdfFront();
                    Toast.makeText(
                                    ResultActivity.this,
                                    "Berhasil menyimpan difolder Download!",
                                    Toast.LENGTH_SHORT)
                            .show();
                });

        BTNColor.setOnClickListener(
                v -> {
                    int currentColor = TXTTitle.getCurrentTextColor();

                    AmbilWarnaDialog colorPickerDialog =
                            new AmbilWarnaDialog(
                                    this,
                                    currentColor,
                                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                                        @Override
                                        public void onOk(AmbilWarnaDialog dialog, int color) {
                                            TXTTitle.setTextColor(color);
                                            TXTSubTitle.setTextColor(color);
                                            TXTNama.setTextColor(color);
                                            TXTKota.setTextColor(color);
                                            TXTLahir.setTextColor(color);
                                            TXTKelamin.setTextColor(color);
                                            TXTLaku.setTextColor(color);
                                            TXTName.setTextColor(color);
                                            TXTCity.setTextColor(color);
                                            TXTBorn.setTextColor(color);
                                            TXTGender.setTextColor(color);
                                            TXTExpired.setTextColor(color);
                                            TXTNik.setTextColor(color);
                                            TXTTitleA.setTextColor(color);
                                            TXTSubTitleA.setTextColor(color);
                                        }

                                        @Override
                                        public void onCancel(AmbilWarnaDialog dialog) {
                                            // Handle cancellation if needed
                                        }
                                    });

                    colorPickerDialog.show();
                });

        BTNBackground.setOnClickListener(
                v -> {
                    Intent intent =
                            new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE_10);
                });
    }

    private void createPdfBack() {
        PdfDocument document = new PdfDocument();
        View content = findViewById(R.id.contentBack);

        // Use the content size for PdfDocument.PageInfo and Bitmap
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), 1)
                        .create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Bitmap bitmap =
                Bitmap.createBitmap(
                        content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        content.draw(canvas);

        page.getCanvas().drawBitmap(bitmap, 0, 0, null);

        document.finishPage(page);

        String fileName = "back.pdf";
        File downloadFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(downloadFolder, fileName);

        try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
            document.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();
    }

    private void createPdfFront() {
        PdfDocument document = new PdfDocument();
        View content = findViewById(R.id.contentFront);

        // Use the content size for PdfDocument.PageInfo and Bitmap
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(content.getWidth(), content.getHeight(), 1)
                        .create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Bitmap bitmap =
                Bitmap.createBitmap(
                        content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        content.draw(canvas);

        page.getCanvas().drawBitmap(bitmap, 0, 0, null);

        document.finishPage(page);

        String fileName = "front.pdf";
        File downloadFolder =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(downloadFolder, fileName);

        try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
            document.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST_CODE_10 && data != null) {
                Uri selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    try {
                        Bitmap bitmap =
                                MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(), selectedImageUri);

                        bgBack.setImageBitmap(bitmap);
                        bgFront.setImageBitmap(bitmap);

                        Toast.makeText(
                                        ResultActivity.this,
                                        "Berhasil mengubah background!",
                                        Toast.LENGTH_SHORT)
                                .show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void generateQRCode(String data, ImageView imageView) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            BitMatrix bitMatrix = barcodeEncoder.encode(data, BarcodeFormat.CODE_128, 550, 80);
            Bitmap bitmap =
                    Bitmap.createBitmap(
                            bitMatrix.getWidth(), bitMatrix.getHeight(), Bitmap.Config.RGB_565);
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                for (int y = 0; y < bitMatrix.getHeight(); y++) {
                    int pixelColor = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                    bitmap.setPixel(x, y, pixelColor);
                }
            }
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
