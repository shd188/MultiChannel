import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 渠道工具类
 * Created by shd on 2016/6/28.
 */
public class ChannelUtils {
    public static final String Version = "version";

    public static final String Channel = "channel";

    public static final String DEFAULT_CHANNEL = "yunxiao";

    public static final String Channel_File = "channel";

    public static String getChannelFromMeta(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF") && entryName.contains("channel_")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split = ret.split("_");
        if (split != null && split.length >= 2) {
            return ret.substring(split[0].length() + 1);
        } else {
            return DEFAULT_CHANNEL;
        }
    }

    /**
     * 得到渠道名
     *
     * @param mContext
     * @return
     */
    public static String getChannel(Context mContext) {
        String channel = "";
        if (isNewVersion(mContext)) {//是新版本
            Log.e("isNewVersion  %s", "isNewVersion");
            saveChannel(mContext);//保存当前版本
            channel = getChannelFromMeta(mContext);
        } else {
            channel = getCachChannel(mContext);
        }
        return channel;
    }

    /**
     * 保存当前的版本号和渠道名
     *
     * @param mContext
     */
    public static void saveChannel(Context mContext) {
        SharedPreferences mSettinsSP = mContext.getSharedPreferences(Channel_File, Activity.MODE_PRIVATE);
        SharedPreferences.Editor mSettinsEd = mSettinsSP.edit();
        mSettinsEd.putString(Version, PhoneInformationUtils.getAppVersionName(mContext));
        mSettinsEd.putString(Channel, getChannelFromMeta(mContext));
        //提交保存
        mSettinsEd.commit();
    }

    private static boolean isNewVersion(Context mContext) {
        SharedPreferences mSettinsSP = mContext.getSharedPreferences(Channel_File, Activity.MODE_PRIVATE);
        String version = PhoneInformationUtils.getAppVersionName(mContext);
        Log.e("version%s", version);
        return !mSettinsSP.getString(Version, "").equals(version);
    }

    private static String getCachChannel(Context mContext) {
        SharedPreferences mSettinsSP = mContext.getSharedPreferences(Channel_File, Activity.MODE_PRIVATE);
        return mSettinsSP.getString(Channel, DEFAULT_CHANNEL);
    }
}
