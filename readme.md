
### DEMO 总结
1、MediaPlayer编解码、SurfaceTexture渲染
    * >=8.0版本,MediaPlayer.SEEK_CLOSEST的seek模式比exo的要流畅
    
2、Mediacodec编解码、SurfaceTexture渲染，基于[GoldenVideoClip](https://github.com/meiliqin/GoldenVideoClip) 思路开发
    * 抽帧预览,如果下面seekbar拖动太快了,解码渲染跟不上,会有跳帧处理,seek回退时不做解码预览
    * 无法实现基本编辑预览、Golden项目只有concat操作,采用切换播放源方式
    * 与当前exo结合预览的话,整体不统一,开发维护也比较麻烦
    * 后期对比抽帧速度（ffmpeg、Glide）、替换现在的抽帧列表方案 
    
3、MeiShe SDK
    * 可解决片段播放过渡卡顿问题、逐帧预览问题
    * 满足基本编辑(concat、clip、speed、split、copy、图片预览)  
    * 转场、特效的结合
    * 画布编辑操作
    * 收费项目,必剪和splice在用         