package baiduocr.bosd.com.ocrdemo;

import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;

import java.io.File;
import java.io.FilenameFilter;

public class MainActivity extends AppCompatActivity
{
    private AlertDialog.Builder alertDialog;

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void infoPopText(final String result) {
        alertText("", result);
    }

    private void initAccessToken()
    {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>()
        {
            @Override
            public void onResult(AccessToken accessToken)
            {
                String token = accessToken.getAccessToken();
            }

            @Override
            public void onError(OCRError error)
            {
                error.printStackTrace();
                alertText("licence方式获取token失败", error.getMessage());
            }
        }, getApplicationContext());
    }

    private void initAccessTokenWithAkSk()
    {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>()
        {
            @Override
            public void onResult(AccessToken result)
            {
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error)
            {
                error.printStackTrace();
                alertText("AK，SK方式获取token失败", error.getMessage());
            }
        }, getApplicationContext(),  "zxoWQTtrFmHhZbo3MhOK1Bay", "Qv0nZtKPnaPKjZV0LDhcPX0wByyh3wBG");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertDialog = new AlertDialog.Builder(this);
        initAccessToken();
        //initAccessTokenWithAkSk();
        final File[] dataSource = getPlateImages();
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(new ListAdapter()
        {
            @Override
            public boolean areAllItemsEnabled()
            {
                return true;
            }

            @Override
            public boolean isEnabled(int i)
            {
                return true;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver)
            {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver)
            {

            }

            @Override
            public int getCount()
            {
                return dataSource.length;
            }

            @Override
            public Object getItem(int i)
            {
                return null;
            }

            @Override
            public long getItemId(int i)
            {
                return 0;
            }

            @Override
            public boolean hasStableIds()
            {
                return false;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup)
            {
                View viewResult = null;
                if(view == null)
                {
                    view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_list_item, null);
                    ImageView imageView = view.findViewById(R.id.imageView);
                    imageView.setImageDrawable(Drawable.createFromPath(dataSource[i].getAbsolutePath()));
                }
                else
                {
                    ImageView imageView = view.findViewById(R.id.imageView);
                    imageView.setImageDrawable(Drawable.createFromPath(dataSource[i].getAbsolutePath()));
                }
                viewResult = view;
                return viewResult;
            }

            @Override
            public int getItemViewType(int i)
            {
                return 0;
            }

            @Override
            public int getViewTypeCount()
            {
                return 1;
            }

            @Override
            public boolean isEmpty()
            {
                return false;
            }
        });
        listView.setClickable(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String path = dataSource[i].getAbsolutePath();
                RecognizeService.recLicensePlate(MainActivity.this,
                        path,
                        new RecognizeService.ServiceListener()
                        {
                            @Override
                            public void onResult(String result)
                            {
                                infoPopText(result);
                            }
                        });
            }
        });

//        String path = Environment.getExternalStorageDirectory() + "/plate_locate.jpg";
//        RecognizeService.recLicensePlate(MainActivity.this,
//                path,
//                //FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
//                new RecognizeService.ServiceListener()
//                {
//                    @Override
//                    public void onResult(String result)
//                    {
//                        infoPopText(result);
//                    }
//                });
//        RecognizeService.recGeneral(MainActivity.this,
//                path,
//                //FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
//                new RecognizeService.ServiceListener()
//                {
//                    @Override
//                    public void onResult(String result)
//                    {
//                        infoPopText(result);
//                    }
//                });
    }

    private File[] getPlateImages()
    {
        File file = Environment.getExternalStorageDirectory();
        return file.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File file, String s)
            {
                return s.endsWith(".jpg");
            }
        });
    }
}
