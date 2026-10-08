package com.uzair.teams;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private WebView web;
    private ProgressBar bar;
    private PermissionRequest pending;
    private ValueCallback<Uri[]> fileCb;
    private String dlUrl, dlUa, dlCd, dlMime;
    private static final int REQ_FILE = 78;
    private static final int REQ_STORAGE = 79;
    private SharedPreferences prefs;
    private static final int REQ = 77;
    private static final int PURPLE = 0xFF5B5FC7;
    private static final int M_RELOAD = 1;
    private static final int M_MIC = 2;
    private static final int M_CAM = 3;
    private static final int M_BG = 4;
    private static final int M_ABOUT = 5;
    private static final int M_DARK = 7;
    private static final String DARK_ON =
            "(function(){var s=document.getElementById('__tgdark');"
            + "if(!s){s=document.createElement('style');s.id='__tgdark';(document.head||document.documentElement).appendChild(s);}"
            + "s.textContent='html{filter:invert(1) hue-rotate(180deg) !important;background:#fff}"
            + "img,video,canvas,picture,[style*=background-image]{filter:invert(1) hue-rotate(180deg) !important}';})();";
    private static final String DARK_OFF =
            "(function(){var s=document.getElementById('__tgdark');if(s)s.remove();})();";
    private static final int M_POP = 6;
    private static final String HOME = "https://teams.cloud.microsoft";
    private static final String UA = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    private static final String CREATOR = "Uzair Nawaz";
    private static final String WEBSITE = "https://github.com/Uzair-Nawaz/Teamsgo";
    private static final String ISSUES = "https://github.com/Uzair-Nawaz/TeamsGo/issues";

    private static final String HIDE_JS =
            "(function(){if(window.__tgPop)return;window.__tgPop=1;"
            + "var st=document.createElement('style');"
            + "st.textContent='[role=tooltip]{display:none !important;visibility:hidden !important}';"
            + "(document.head||document.documentElement).appendChild(st);"
            + "var W=['continue without audio or video','got it','not now','maybe later','no thanks'];"
            + "var P=['have access to your mic','have access to your camera','have access to your microphone'];"
            + "var q=0;"
            + "function closeNear(el){var n=el;for(var d=0;d<7&&n.parentElement;d++){n=n.parentElement;"
            + "var c=n.querySelector('button[aria-label*=lose],button[title*=lose]');"
            + "if(c){c.click();return true;}}n.style.display='none';return true;}"
            + "function sweep(){"
            + "var ds=document.querySelectorAll('[role=dialog],[role=alertdialog]');"
            + "for(var i=0;i<ds.length;i++){var bs=ds[i].querySelectorAll('button');"
            + "for(var j=0;j<bs.length;j++){var t=(bs[j].innerText||'').trim().toLowerCase();"
            + "if(t&&W.indexOf(t)>-1&&bs[j].offsetParent!==null){bs[j].click();return;}}}"
            + "if(!document.body)return;"
            + "var w=document.createTreeWalker(document.body,4,null,false),n;"
            + "while((n=w.nextNode())){var tx=(n.nodeValue||'').toLowerCase();"
            + "for(var k=0;k<P.length;k++){if(tx.indexOf(P[k])>-1&&n.parentElement&&n.parentElement.offsetParent!==null){closeNear(n.parentElement);return;}}}}"
            + "new MutationObserver(function(){if(!q){q=1;setTimeout(function(){q=0;sweep();},1200);}})"
            + ".observe(document.documentElement,{childList:true,subtree:true});"
            + "})();";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("teamsgo", MODE_PRIVATE);
        if (prefs.getBoolean("bg", false)) bgStart();

        getWindow().setStatusBarColor(PURPLE);
        getWindow().setNavigationBarColor(PURPLE);
        if (getActionBar() != null) {
            getActionBar().setBackgroundDrawable(new ColorDrawable(PURPLE));
            SpannableString t = new SpannableString("TeamsGo");
            t.setSpan(new ForegroundColorSpan(0xFFFFFFFF), 0, t.length(), 0);
            getActionBar().setTitle(t);
        }

        FrameLayout root = new FrameLayout(this);
        web = new WebView(this);
        root.addView(web, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(100);
        bar.setIndeterminate(false);
        bar.setProgressTintList(ColorStateList.valueOf(0xFFFFC83D));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (4 * getResources().getDisplayMetrics().density));
        lp.gravity = Gravity.TOP;
        root.addView(bar, lp);
        setContentView(root);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUserAgentString(UA);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setUseWideViewPort(true);
        s.setLoadWithOverviewMode(true);

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(web, true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView v, WebResourceRequest r, WebResourceError e) {
                if (r.isForMainFrame() && !online()) showOffline();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                if (!popupsOn()) return false;
                String sc = r.getUrl().getScheme();
                if (sc == null) return false;
                if (sc.equals("http") || sc.equals("https") || sc.equals("about")
                        || sc.equals("data") || sc.equals("blob") || sc.equals("javascript")) {
                    return false;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                super.onPageFinished(v, url);
                applyDark(v, url);
                if (popupsOn() && url != null && url.startsWith("https://")) {
                    v.evaluateJavascript(HIDE_JS, null);
                }
            }
        });
        web.setDownloadListener((u, ua, cd, mime, len) -> startDownload(u, ua, cd, mime));
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView v, int p) {
                bar.setProgress(p);
                bar.setVisibility(p < 100 ? View.VISIBLE : View.GONE);
            }

            @Override
            public boolean onJsAlert(WebView v, String url, String message, JsResult result) {
                if (popupsOn()) {
                    result.confirm();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onJsBeforeUnload(WebView v, String url, String message, JsResult result) {
                if (popupsOn()) {
                    result.confirm();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p) {
                if (fileCb != null) fileCb.onReceiveValue(null);
                fileCb = cb;
                try {
                    startActivityForResult(p.createIntent(), REQ_FILE);
                } catch (Exception e) {
                    fileCb = null;
                    return false;
                }
                return true;
            }

            @Override
            public void onPermissionRequest(final PermissionRequest r) {
                runOnUiThread(() -> handle(r));
            }

            @Override
            public void onPermissionRequestCanceled(PermissionRequest r) {
                if (pending == r) pending = null;
            }
        });

        load(getIntent());
    }

    private void bgStart() {
        try {
            startForegroundService(new Intent(this, BackgroundService.class));
        } catch (Exception e) { }
    }

    private void bgStop() {
        try {
            stopService(new Intent(this, BackgroundService.class));
        } catch (Exception e) { }
    }

    private void askBattery() {
        try {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())) return;
            int shown = prefs.getInt("bgInfo", 0);
            if (shown >= 2) return;
            prefs.edit().putInt("bgInfo", shown + 1).apply();
            new AlertDialog.Builder(this)
                    .setTitle("Keep TeamsGo alive")
                    .setMessage("Tap Open, then set TeamsGo to Allow / No restrictions. Also open your phone battery settings (App launch or Auto-start), find TeamsGo, and allow it to run in the background.")
                    .setPositiveButton("Open", (dlg, which) -> startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)))
                    .setNegativeButton("Later", null)
                    .show();
        } catch (Exception e) { }
    }

    private void startDownload(String url, String ua, String cd, String mime) {
        if (url == null) return;
        if (url.startsWith("blob:") || url.startsWith("data:")) {
            Toast.makeText(this, "This download type is not supported yet", Toast.LENGTH_LONG).show();
            return;
        }
        if (Build.VERSION.SDK_INT < 29
                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            dlUrl = url; dlUa = ua; dlCd = cd; dlMime = mime;
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_STORAGE);
            return;
        }
        try {
            String name = URLUtil.guessFileName(url, cd, mime);
            DownloadManager.Request rq = new DownloadManager.Request(Uri.parse(url));
            if (mime != null && mime.length() > 0) rq.setMimeType(mime);
            String ck = CookieManager.getInstance().getCookie(url);
            if (ck != null) rq.addRequestHeader("Cookie", ck);
            if (ua != null) rq.addRequestHeader("User-Agent", ua);
            rq.setTitle(name);
            rq.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            rq.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
            ((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).enqueue(rq);
            Toast.makeText(this, "Downloading " + name, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int code, int res, Intent data) {
        if (code == REQ_FILE) {
            if (fileCb != null) {
                Uri[] r = null;
                if (res == RESULT_OK) r = WebChromeClient.FileChooserParams.parseResult(res, data);
                fileCb.onReceiveValue(r);
                fileCb = null;
            }
            return;
        }
        super.onActivityResult(code, res, data);
    }

    private void applyDark(WebView v, String url) {
        if (v == null || url == null || !url.startsWith("https://")) return;
        v.evaluateJavascript(prefs.getBoolean("dark", false) ? DARK_ON : DARK_OFF, null);
    }

    private boolean popupsOn() {
        return prefs.getBoolean("pop", true);
    }

    private boolean online() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm == null ? null : cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    private void showOffline() {
        String html = "<html><head><meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<style>body{margin:0;height:100vh;display:flex;flex-direction:column;align-items:center;"
                + "justify-content:center;background:#5B5FC7;color:#fff;font-family:sans-serif;text-align:center;padding:24px;box-sizing:border-box}"
                + ".e{font-size:72px}h2{margin:16px 0 8px;font-size:24px}p{font-size:18px;margin:0 0 28px}"
                + "a{background:#FFC83D;color:#222;font-weight:bold;text-decoration:none;padding:14px 28px;border-radius:30px;font-size:18px}"
                + "</style></head><body>"
                + "<div class='e'>&#128225;&#128584;</div>"
                + "<h2><b>Seems you are out of internet access!</b></h2>"
                + "<p><b>Make sure you are connected &#9888;&#65039;</b></p>"
                + "<a href='" + HOME + "'>&#128260; Try again</a>"
                + "</body></html>";
        web.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        m.add(0, M_RELOAD, 0, "Reload");
        m.add(0, M_MIC, 1, "Allow microphone")
                .setCheckable(true)
                .setChecked(prefs.getBoolean("mic", false));
        m.add(0, M_CAM, 2, "Allow camera")
                .setCheckable(true)
                .setChecked(prefs.getBoolean("cam", false));
        m.add(0, M_POP, 3, "Hide popups")
                .setCheckable(true)
                .setChecked(popupsOn());
        m.add(0, M_BG, 4, "Save in background")
                .setCheckable(true)
                .setChecked(prefs.getBoolean("bg", false));
        m.add(0, M_DARK, 5, "Dark site")
                .setCheckable(true)
                .setChecked(prefs.getBoolean("dark", false));
        m.add(0, M_ABOUT, 6, "About Me");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem it) {
        boolean on;
        switch (it.getItemId()) {
            case M_RELOAD:
                if (!online()) {
                    showOffline();
                } else {
                    String u = web.getUrl();
                    if (u == null || u.startsWith("data:") || u.equals("about:blank")) web.loadUrl(HOME);
                    else web.reload();
                }
                return true;
            case M_MIC:
                on = !it.isChecked();
                it.setChecked(on);
                prefs.edit().putBoolean("mic", on).apply();
                Toast.makeText(this, on ? "Microphone: ALLOWED" : "Microphone: BLOCKED (tap Reload to release)",
                        Toast.LENGTH_SHORT).show();
                return true;
            case M_CAM:
                on = !it.isChecked();
                it.setChecked(on);
                prefs.edit().putBoolean("cam", on).apply();
                Toast.makeText(this, on ? "Camera: ALLOWED" : "Camera: BLOCKED (tap Reload to release)",
                        Toast.LENGTH_SHORT).show();
                return true;
            case M_POP:
                on = !it.isChecked();
                it.setChecked(on);
                prefs.edit().putBoolean("pop", on).apply();
                Toast.makeText(this, on ? "Hide popups: ON (tap Reload)" : "Hide popups: OFF (tap Reload)",
                        Toast.LENGTH_SHORT).show();
                return true;
            case M_BG:
                on = !it.isChecked();
                it.setChecked(on);
                prefs.edit().putBoolean("bg", on).apply();
                if (on) { bgStart(); askBattery(); } else { bgStop(); }
                Toast.makeText(this, on ? "Save in background: ON" : "Save in background: OFF",
                        Toast.LENGTH_SHORT).show();
                return true;
            case M_DARK:
                on = !it.isChecked();
                it.setChecked(on);
                prefs.edit().putBoolean("dark", on).apply();
                applyDark(web, web.getUrl());
                Toast.makeText(this, on ? "Dark site: ON" : "Dark site: OFF", Toast.LENGTH_SHORT).show();
                return true;
            case M_ABOUT:
                showAbout();
                return true;
        }
        return super.onOptionsItemSelected(it);
    }

    private void showAbout() {
        String ver = "1.0";
        try {
            ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) { }
        String body = "&#128100; <b>Creator:</b> " + CREATOR + "<br><br>"
                + "&#127760; <b>Website:</b> <a href=\"" + WEBSITE + "\">" + WEBSITE.replace("https://", "") + "</a><br><br>"
                + "&#128030; <b>Report Any Issue At:</b> <a href=\"" + ISSUES + "\">" + ISSUES.replace("https://", "") + "</a><br><br>"
                + "&#128230; <b>Version:</b> " + ver;
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("&#128584; <b>About Me</b>", Html.FROM_HTML_MODE_LEGACY))
                .setMessage(Html.fromHtml(body, Html.FROM_HTML_MODE_LEGACY))
                .setPositiveButton("OK", null)
                .create();
        d.show();
        TextView tv = (TextView) d.findViewById(android.R.id.message);
        if (tv != null) tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private boolean wanted(String res) {
        if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(res)) return prefs.getBoolean("mic", false);
        if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(res)) return prefs.getBoolean("cam", false);
        return false;
    }

    private void handle(PermissionRequest r) {
        List<String> need = new ArrayList<>();
        boolean blocked = false;
        boolean any = false;
        for (String res : r.getResources()) {
            boolean isMedia = PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(res)
                    || PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(res);
            if (!isMedia) continue;
            if (!wanted(res)) { blocked = true; continue; }
            any = true;
            if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(res)
                    && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                need.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(res)
                    && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                need.add(Manifest.permission.CAMERA);
            }
        }
        if (blocked) {
            Toast.makeText(this, "Teams asked for mic/camera. Turn it on in the 3‑dot menu, then Reload.",
                    Toast.LENGTH_LONG).show();
        }
        if (!any) { r.deny(); return; }
        if (need.isEmpty()) {
            String[] ok = allowed(r);
            if (ok.length > 0) r.grant(ok);
            else r.deny();
        } else {
            pending = r;
            requestPermissions(need.toArray(new String[0]), REQ);
        }
    }

    private String[] allowed(PermissionRequest r) {
        List<String> ok = new ArrayList<>();
        for (String res : r.getResources()) {
            if (!wanted(res)) continue;
            if (PermissionRequest.RESOURCE_AUDIO_CAPTURE.equals(res)
                    && checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                ok.add(res);
            }
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE.equals(res)
                    && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                ok.add(res);
            }
        }
        return ok.toArray(new String[0]);
    }

    @Override
    public void onRequestPermissionsResult(int code, String[] perms, int[] results) {
        super.onRequestPermissionsResult(code, perms, results);
        if (code == REQ_STORAGE) {
            if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED && dlUrl != null) startDownload(dlUrl, dlUa, dlCd, dlMime);
            else Toast.makeText(this, "Storage permission is needed to download", Toast.LENGTH_SHORT).show();
            return;
        }
        if (code == REQ && pending != null) {
            String[] ok = allowed(pending);
            if (ok.length > 0) pending.grant(ok);
            else pending.deny();
            pending = null;
        }
    }

    private void load(Intent i) {
        String url = HOME;
        Uri d = i == null ? null : i.getData();
        if (d != null && d.getHost() != null) {
            String h = d.getHost();
            if (h.equals("teams.microsoft.com") || h.equals("teams.live.com") || h.equals("teams.cloud.microsoft")) {
                url = d.toString().replace("//teams.microsoft.com", "//teams.cloud.microsoft");
            }
        }
        if (!online()) { showOffline(); return; }
        web.loadUrl(url);
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        if (i != null && i.getData() != null) load(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieManager.getInstance().flush();
        if (prefs.getBoolean("bg", false) && web != null) { web.onResume(); web.resumeTimers(); }
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else if (prefs.getBoolean("bg", false)) moveTaskToBack(true);
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        bgStop();
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
