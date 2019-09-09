# java-for-Mzitu

新人第一次抛弃书本使用github，所以分类有点问题

那个bin目录和根目录是一样的

所以样子有点怪，别介意啊，使用的编译器是notepad++手撸出来的，所以没有那些有关项目的bin之类的东西，只能够手动创建，但我也没按标准分类，别介意啊

**因此仅看根目录即可**



这个新手学习Java爬虫时，借鉴各位大佬的代码弄出来的东西

**借鉴一、** <https://www.52pojie.cn/forum.php?mod=viewthread&tid=856079&extra=page%3D2%26filter%3Dtypeid%26typeid%3D192>

**借鉴二、** https://blog.csdn.net/dangerous_fire/article/details/63251212

**借鉴三、** https://www.jianshu.com/p/c38ddd4259c4
**借鉴四、** https://www.jianshu.com/p/8f5287bea0f5

感谢各位大佬！！！



该功能是获取该妹子图(https://www.mzitu.com/)首页上的总页数

然后再将每个页数上的图库的图源地址写入到address.txt本地文件上

然后再读取这个文件，去获取每个图集中的全部图片



由于害怕403的原因，导致sleep();方法的睡眠时间有点长

同时，这也是只敢开三个线程去爬取的原因，也是慢的原因之一吧



ps:如果只想一次运行成功，直接在E盘下创建op\爬取即可，然后直接编译Mz.java即可



**最后，希望各位客官不要嫌弃吧**