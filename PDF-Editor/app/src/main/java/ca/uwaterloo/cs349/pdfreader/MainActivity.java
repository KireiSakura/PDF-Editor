package ca.uwaterloo.cs349.pdfreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

// PDF sample code from
// https://medium.com/@chahat.jain0/rendering-a-pdf-document-in-android-activity-fragment-using-pdfrenderer-442462cb8f9a
// Issues about cache etc. are not at all obvious from documentation, so read this carefully.

public class MainActivity extends AppCompatActivity {

    final String LOGNAME = "pdf_viewer";
    final String FILENAME = "shannon1948.pdf";
    final int FILERESID = R.raw.shannon1948;
    float scaleFactor = 1.0f;
    ImageView pen, highlighter, erase, undo, redo,cursor;

    // manage the pages of the PDF, see below
    PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;
    private PdfRenderer.Page currentPage;
    PDFimage pageImage;
    ArrayList <PDFimage> pdFimages = new ArrayList<>();
    int index;
    ImageButton up;
    ImageButton down;
    // custom ImageView class that captures strokes and draws them over the image

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layout = findViewById(R.id.pdfLayout);
        for (int i = 0; i < 55; i++) {
            pdFimages.add(new PDFimage(this));
        }
        pageImage = new PDFimage(this);
        layout.addView(pageImage);
        layout.setEnabled(true);
        pageImage.setMinimumWidth(1000);
        pageImage.setMinimumHeight(2000);
        index = 0;
        // open page 0 of the PDF
        // it will be displayed as an image in the pageImage (above)
        try {
            openRenderer(this);
            storeBitmaps();
            showPage(index);
            //closeRenderer();
        } catch (IOException exception) {
            Log.d(LOGNAME, "Error opening PDF");
        }
        final TextView page = findViewById(R.id.page);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
        up.setEnabled(false);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(!down.isEnabled()){
                        down.setEnabled(true);
                    }
                    pdFimages.get(index).setPath(pageImage.getPaths());
                    pdFimages.get(index).setHighlightPath(pageImage.getHighlightPaths());
                    pdFimages.get(index).setUndoStack(pageImage.getUndoStack());
                    pdFimages.get(index).setRedoStack(pageImage.getRedoStack());
                    pdFimages.get(index).setEraseDraw(pageImage.getEraseDraw());
                    pdFimages.get(index).setEraseDrawRedo(pageImage.getEraseDrawRedo());
                    pdFimages.get(index).setEraseHighlight(pageImage.getEraseHighlight());
                    pdFimages.get(index).setEraseHighlightRedo(pageImage.getEraseHighlightRedo());
                    index--;
                    if(index == 0){
                        up.setEnabled(false);
                    }
                    page.setText((index+1) + "/55");
                    showPage(index);
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(!up.isEnabled()){
                        up.setEnabled(true);
                    }
                    pdFimages.get(index).setPath(pageImage.getPaths());
                    pdFimages.get(index).setHighlightPath(pageImage.getHighlightPaths());
                    pdFimages.get(index).setUndoStack(pageImage.getUndoStack());
                    pdFimages.get(index).setRedoStack(pageImage.getRedoStack());
                    pdFimages.get(index).setEraseDraw(pageImage.getEraseDraw());
                    pdFimages.get(index).setEraseDrawRedo(pageImage.getEraseDrawRedo());
                    pdFimages.get(index).setEraseHighlight(pageImage.getEraseHighlight());
                    pdFimages.get(index).setEraseHighlightRedo(pageImage.getEraseHighlightRedo());
                    index++;
                    if(index == 54){
                        down.setEnabled(false);
                    }
                    page.setText((index+1) + "/55");
                    showPage(index);
                }
        });

        pen = findViewById(R.id.pen);
        highlighter = findViewById(R.id.highlighter);
        erase = findViewById(R.id.eraser);
        undo = findViewById(R.id.undo);
        redo = findViewById(R.id.redo);
        cursor = findViewById(R.id.cursor);
        cursor.setBackgroundColor(Color.LTGRAY);
        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageImage.pen = 1;
                pageImage.highlighter = 0;
                pageImage.erase = 0;
                pageImage.undo = 0;
                pageImage.redo = 0;
                pageImage.erasePath = null;
                pen.setBackgroundColor(Color.LTGRAY);
                highlighter.setBackgroundColor(Color.TRANSPARENT);
                erase.setBackgroundColor(Color.TRANSPARENT);
                undo.setBackgroundColor(Color.TRANSPARENT);
                redo.setBackgroundColor(Color.TRANSPARENT);
                cursor.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        highlighter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageImage.pen = 0;
                pageImage.highlighter = 1;
                pageImage.erase = 0;
                pageImage.undo = 0;
                pageImage.redo = 0;
                pageImage.erasePath = null;
                pen.setBackgroundColor(Color.TRANSPARENT);
                highlighter.setBackgroundColor(Color.LTGRAY);
                erase.setBackgroundColor(Color.TRANSPARENT);
                undo.setBackgroundColor(Color.TRANSPARENT);
                redo.setBackgroundColor(Color.TRANSPARENT);
                cursor.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        erase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageImage.pen = 0;
                pageImage.highlighter = 0;
                pageImage.erase = 1;
                pageImage.undo = 0;
                pageImage.redo = 0;
                pen.setBackgroundColor(Color.TRANSPARENT);
                highlighter.setBackgroundColor(Color.TRANSPARENT);
                erase.setBackgroundColor(Color.LTGRAY);
                undo.setBackgroundColor(Color.TRANSPARENT);
                redo.setBackgroundColor(Color.TRANSPARENT);
                cursor.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageImage.pen = 0;
                pageImage.highlighter = 0;
                pageImage.erase = 0;
                pageImage.undo = 1;
                pageImage.redo = 0;
                pageImage.erasePath = null;
                pageImage.undo();
                pen.setBackgroundColor(Color.TRANSPARENT);
                highlighter.setBackgroundColor(Color.TRANSPARENT);
                erase.setBackgroundColor(Color.TRANSPARENT);
                redo.setBackgroundColor(Color.TRANSPARENT);
                cursor.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageImage.pen = 0;
                pageImage.highlighter = 0;
                pageImage.erase = 0;
                pageImage.undo = 0;
                pageImage.redo = 1;
                pageImage.erasePath = null;
                pageImage.redo();
                pen.setBackgroundColor(Color.TRANSPARENT);
                highlighter.setBackgroundColor(Color.TRANSPARENT);
                erase.setBackgroundColor(Color.TRANSPARENT);
                undo.setBackgroundColor(Color.TRANSPARENT);
                cursor.setBackgroundColor(Color.TRANSPARENT);
            }
        });
        cursor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageImage.pen = 0;
                pageImage.highlighter = 0;
                pageImage.erase = 0;
                pageImage.undo = 0;
                pageImage.redo = 0;
                pageImage.erasePath = null;
                pen.setBackgroundColor(Color.TRANSPARENT);
                highlighter.setBackgroundColor(Color.TRANSPARENT);
                erase.setBackgroundColor(Color.TRANSPARENT);
                undo.setBackgroundColor(Color.TRANSPARENT);
                redo.setBackgroundColor(Color.TRANSPARENT);
                cursor.setBackgroundColor(Color.LTGRAY);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStop() {
        super.onStop();
        try {
            closeRenderer();
        } catch (IOException ex) {
            Log.d(LOGNAME, "Unable to close PDF renderer");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void openRenderer(Context context) throws IOException {
        // In this sample, we read a PDF from the assets directory.
        File file = new File(context.getCacheDir(), FILENAME);
        if (!file.exists()) {
            // pdfRenderer cannot handle the resource directly,
            // so extract it into the local cache directory.
            InputStream asset = this.getResources().openRawResource(FILERESID);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

        // capture PDF data
        // all this just to get a handle to the actual PDF representation
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
        }
    }

    // do this before you quit!
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeRenderer() throws IOException {
        if (null != currentPage) {
            currentPage.close();
        }
        pdfRenderer.close();
        parcelFileDescriptor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void storeBitmaps() {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        for (int i = 0; i < 55; i++) {
            // Use `openPage` to open a specific page in PDF.
            currentPage = pdfRenderer.openPage(i);
            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            pdFimages.get(i).setImage(bitmap);
            currentPage.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }

        // Display the page
        pageImage.setImage(pdFimages.get(index).getBitmap());
        pageImage.setPath(pdFimages.get(index).getPaths());
        pageImage.setHighlightPath(pdFimages.get(index).getHighlightPaths());
        pageImage.setUndoStack(pdFimages.get(index).getUndoStack());
        pageImage.setRedoStack(pdFimages.get(index).getRedoStack());
        pageImage.setEraseDraw(pdFimages.get(index).getEraseDraw());
        pageImage.setEraseHighlight(pdFimages.get(index).getEraseHighlight());
        pageImage.setEraseDrawRedo(pdFimages.get(index).getEraseDrawRedo());
        pageImage.setEraseHighlightRedo(pdFimages.get(index).getEraseHighlightRedo());
    }
}
