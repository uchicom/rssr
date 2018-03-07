# rssr
RSS Reader

rssの追加をして、ポーリング間隔を設定する。最新情報が追加されたら、データを更新する。同じ情報はそのまま。

キャッシュ方法
rssのurlはテキストファイルに保存する。
a=http://a.com
b=http://b.com/rss
c=http://c.com/rss

pubDate
dc:dateをpubDateに格納