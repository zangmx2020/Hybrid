package com.example.jsbridge;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText editText;
    private Button showBtn;
    private Button refreshBtn;
    private MainActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        editText = findViewById(R.id.editText);
        showBtn = findViewById(R.id.showBtn);
        refreshBtn = findViewById(R.id.refreshBtn);

        webView.loadUrl("http://192.168.1.9:8080?timestamp=" + new Date().getTime());
        webView.getSettings().setJavaScriptEnabled(true);

//        JSBridge - URL Scheme 拦截
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
//                if (!message.startsWith("jsbridge://")) {
//                    return super.onJsAlert(view, url, message, result);
//                }
//
//                String text = message.substring(message.indexOf("=") + 1);
//                self.showNativeDialog(text);
//
//                result.confirm();
//                return true;
//            }
//        });

//      JSBridge - API 注入
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new NativeBridge(this), "NativeBridge");

//      显示btn
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputValue = editText.getText().toString();
                self.showWebDialog(inputValue);
            }
        });

//      刷新btn
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://192.168.1.9:8080?timestamp=" + new Date().getTime());
            }
        });
    }

//    URL Scheme 拦截
    private void showWebDialog(String text) {
        String jsCode = String.format("window.showWebDialog('%s')", text);
        webView.evaluateJavascript(jsCode, null);
    }

    private void showNativeDialog(String text) {
        new AlertDialog.Builder(this).setMessage(text).create().show();
    }

//    API 注入
    class NativeBridge {
        private Context ctx;
        NativeBridge(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void showNativeDialog(String text) {
            new AlertDialog.Builder(ctx).setMessage(text).create().show();
        }
    }
}
