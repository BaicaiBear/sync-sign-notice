package top.bearcabbage.syncsignnotice;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.sql.Timestamp;

/**
 * 新闻项类 - 根据JS脚本中的jellynews表结构
 */
@Environment(EnvType.CLIENT)
public class NewsItem {
    public Timestamp createdAt;  // created_at
    public String type;          // type (标签)
    public String name;          // name (作者署名)
    public String content;       // content
}
