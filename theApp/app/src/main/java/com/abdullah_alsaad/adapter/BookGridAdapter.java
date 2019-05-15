package com.abdullah_alsaad.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abdullah_alsaad.JavaClass.BookItem;
import com.abdullah_alsaad.OrmLiteDatabseHelper.DbPer;
import com.abdullah_alsaad.R;
import com.abdullah_alsaad.activities.FragmentsActivity;
import com.abdullah_alsaad.fragment.Book;
import com.abdullah_alsaad.fragment.PdfViewer;
import com.abdullah_alsaad.generic.AppUtil;
import com.abdullah_alsaad.generic.StorageHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookGridAdapter extends RecyclerView.Adapter<BookGridAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private Fragment pdfViewFragment;
    private FragmentManager fragmentManager;
    private ProgressBar progressBar;
    private boolean taskRunning;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<BookItem> bookItemsList;
    private Fragment book;
    private Dialog alertDialog;


    public BookGridAdapter(Context context, List<BookItem> bookItemsList, FragmentManager fragmentManager, ProgressBar progressBar, Book book) {
        this.context = context;
        this.pdfViewFragment = new PdfViewer();
        this.fragmentManager = fragmentManager;
        this.progressBar = progressBar;
        this.bookItemsList = bookItemsList;
        this.taskRunning = false;
        this.book = book;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return  new ViewHolder(layoutInflater.inflate(R.layout.book_grid_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final BookItem bookItem = bookItemsList.get(position);

        holder.bookTitle.setText(bookItem.getName());
        byte[] bookImageByte = bookItem.getBookImageByte();
        if(null != bookImageByte){
            Bitmap bmp = BitmapFactory.decodeByteArray(bookImageByte,0,bookImageByte.length);
            holder.bookImage.setImageBitmap(bmp);
        }

        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskRunning == false) {
                    if (AppUtil.checkNetwork(context) == true) {
                        taskRunning = true;
                        progressBar.setVisibility(View.VISIBLE);
                        Download task = new Download(bookItem, holder.btnDelete, holder.btnDownload);
                        task.execute(bookItem.getPdfURL());
                    }
                } else {
                    AppUtil.showToast(context, R.string.downloadInProgress);
                }
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePDFFromDirectory(bookItem, holder.btnDelete, holder.btnDownload);
            }
        });
        if (bookItem.getPdfDownloadPath() == null || bookItem.getPdfDownloadPath().isEmpty()) {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.VISIBLE);
        } else {
            holder.btnDownload.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        }

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            holder.btnDeleteFromFireStore.setVisibility(View.VISIBLE);
            holder.btnDeleteFromFireStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteBook(bookItem.getStringId(), bookItem.getId(), position);
                }
            });
        }

        holder.bookTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StorageHandler.pdfExists(bookItem.getId()) && taskRunning == false) {
                    FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) context;
                    fragmentsActivity.callFragment("PdfViewer", bookItem);
                } else {
                    if (AppUtil.checkNetwork(context) == true) {
                        taskRunning = true;
                        progressBar.setVisibility(View.VISIBLE);
                        Download task = new Download(bookItem, holder.btnDelete, holder.btnDownload);
                        task.execute(bookItem.getPdfURL());
                    } else {
                        AppUtil.showToast(context, R.string.connectToNetwork);
                    }
                }
                // TODO check if pdf file exits
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_place_for_summary, pdfViewFragment, "viewPDF").addToBackStack("viewPDF").commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bookItemsList.size();
    }

//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        final BookItem bookItem = bookItemsList.get(i);
//        final int position = i;
//        View gridView;
//        if (view != null) {
//            gridView = view;
//        } else {
//            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            gridView = layoutInflater.inflate(R.layout.book_grid_item, null);
//        }
//        TextView bookTitle = (TextView) gridView.findViewById(R.id.bookTitle);
//        bookTitle.setText(bookItem.getName());
//        final ImageButton btnDownload = (ImageButton) gridView.findViewById(R.id.btnDownload);
//        final ImageButton btnDelete = (ImageButton) gridView.findViewById(R.id.btnDelete);
//        final ImageButton btnDeleteFromFireStore = (ImageButton) gridView.findViewById(R.id.btnDeleteFromFireStore);
//
//        final CircleImageView bookImage = (CircleImageView) gridView.findViewById(R.id.bookImage);
////        bookImage.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                openBookImageInLargeSize(bookItem.getId());
////            }
////        });
//        byte[] bookImageByte = bookItem.getBookImageByte();
//        if(null != bookImageByte){
//            Bitmap bmp = BitmapFactory.decodeByteArray(bookImageByte,0,bookImageByte.length);
//            bookImage.setImageBitmap(bmp);
//        }
//
////        File file = new File(Environment.getExternalStorageDirectory() +
////                File.separator + "abdullahAlSaad/png/" + bookItem.getId() + ".png");
////        if (file.exists()) {
////            Bitmap bmp = BitmapFactory.decodeByteArray(bookImageByte,0,bookImageByte.length);
////
////        }
//
//        btnDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (taskRunning == false) {
//                    if (AppUtil.checkNetwork(context) == true) {
//                        taskRunning = true;
//                        progressBar.setVisibility(View.VISIBLE);
//                        Download task = new Download(bookItem, btnDelete, btnDownload);
//                        task.execute(bookItem.getPdfURL());
//                    }
//                } else {
//                    AppUtil.showToast(context, R.string.downloadInProgress);
//                }
//            }
//        });
//        btnDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                deletePDFFromDirectory(bookItem, btnDelete, btnDownload);
//            }
//        });
//        if (bookItem.getPdfDownloadPath() == null || bookItem.getPdfDownloadPath().isEmpty()) {
//            btnDelete.setVisibility(View.GONE);
//            btnDownload.setVisibility(View.VISIBLE);
//        } else {
//            btnDownload.setVisibility(View.GONE);
//            btnDelete.setVisibility(View.VISIBLE);
//        }
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            btnDeleteFromFireStore.setVisibility(View.VISIBLE);
//            btnDeleteFromFireStore.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    deleteBook(bookItem.getStringId(), bookItem.getId(), position);
//                }
//            });
//        }
//
//        bookTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (StorageHandler.pdfExists(bookItem.getId()) && taskRunning == false) {
//                    FragmentsActivity fragmentsActivity = (FragmentsActivity) (Activity) context;
//                    fragmentsActivity.callFragment("PdfViewer", bookItem);
//                } else {
//                    if (AppUtil.checkNetwork(context) == true) {
//                        taskRunning = true;
//                        progressBar.setVisibility(View.VISIBLE);
//                        Download task = new Download(bookItem, btnDelete, btnDownload);
//                        task.execute(bookItem.getPdfURL());
//                    } else {
//                        AppUtil.showToast(context, R.string.connectToNetwork);
//                    }
//                }
//                // TODO check if pdf file exits
////                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                fragmentTransaction.replace(R.id.fragment_place_for_summary, pdfViewFragment, "viewPDF").addToBackStack("viewPDF").commit();
//            }
//        });
//        return gridView;
//    }

    private void deleteBook(final String stringId, final long id, final int position) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirm_delete)
                .setMessage(R.string.you_cant_undo_this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (AppUtil.checkNetwork(context) != true) {
                            AppUtil.showToast(context, R.string.noConnectionFound);
                            return;
                        }
                        db.collection(AppUtil.getDevMode("BookItem"))
                                .document(stringId)
                                .delete();
                        AppUtil.showToast(context, R.string.book_deleted_from_backend);
                        DbPer.deleteBookItemToLocalDatabase(context, (int) id);
                        bookItemsList.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void openBookImageInLargeSize(long id) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View largeImageView = layoutInflater.inflate(R.layout.large_image_view, null);
        ImageView largeImage = (ImageView) largeImageView.findViewById(R.id.largeImage);
        File f = new File(Environment.getExternalStorageDirectory() +
                File.separator + "abdullahAlSaad/pdf/" + id + ".png");
        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
        largeImage.setImageBitmap(bmp);
        alertDialog = new Dialog((Activity) context);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(largeImageView);
//        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        alertDialog.show();
    }

    private void deletePDFFromDirectory(BookItem bookItem, ImageButton btnDelete, ImageButton btnDownload) {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "abdullahAlSaad/pdf/" + bookItem.getId() + ".pdf");
        boolean success = true;
        if (folder.exists()) {
            success = folder.delete();
        }
        if (success) {
            bookItem.setPdfDownloadPath("");
            DbPer.saveBookItemToLocalDatabase(context, bookItem);
            btnDelete.setVisibility(View.GONE);
            btnDownload.setVisibility(View.VISIBLE);
        }
    }

//    @Override
//    public int getCount() {
//        return bookItemsList.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return bookItemsList.get(i);
//    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Download extends AsyncTask<String, String, String> {
        //TODO check permission
        BookItem bookItem;
        ImageButton btnDownload = null;
        ImageButton btnDelete = null;

        public Download(BookItem bookItem, ImageButton btnDelete, ImageButton btnDownload) {
            this.bookItem = bookItem;
            this.btnDownload = btnDownload;
            this.btnDelete = btnDelete;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("Starting download");
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String root = Environment.getExternalStorageDirectory().toString();

                System.out.println("Downloading");
                URL url = new URL(f_url[0]);

                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                // Output stream to write file
                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "abdullahAlSaad/pdf");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    OutputStream output = new FileOutputStream(root + "/abdullahAlSaad/pdf/" + bookItem.getId() + ".pdf");
                    byte data[] = new byte[1124];
                    long total = 0;


                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // writing data to file
                        output.write(data, 0, count);
                    }
                    // flushing output
                    output.flush();
                    // closing streams
                    output.close();
                    input.close();
                    bookItem.setPdfDownloadPath(root + "/abdullahAlSaad/pdf/" + bookItem.getId() + ".pdf");
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        /**
         * After completing background task
         **/
        @Override
        protected void onPostExecute(String file_url) {
            System.out.println("Downloaded");
            progressBar.setVisibility(View.INVISIBLE);
            btnDownload.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
            taskRunning = false;
            DbPer.saveBookItemToLocalDatabase(context, bookItem);

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View parentView;
        private TextView bookTitle;
        private final ImageButton btnDownload;
        private ImageButton btnDelete;
        private ImageButton btnDeleteFromFireStore;
        private CircleImageView bookImage;
        public ViewHolder(@NonNull View view){
            super(view);
            this.parentView = view;
            this.bookTitle = (TextView) view.findViewById(R.id.bookTitle);
            this.btnDownload = (ImageButton) view.findViewById(R.id.btnDownload);
            this.btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
            this.btnDeleteFromFireStore = (ImageButton) view.findViewById(R.id.btnDeleteFromFireStore);
            this.bookImage = (CircleImageView) view.findViewById(R.id.bookImage);
        }
    }
}
