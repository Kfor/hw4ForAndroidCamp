# hw4ForAndroidCamp

3170105468-武靖超-安卓开发作业4



完成了Clock题给要求。下简述各部分的实现。

本次作业主要完成了以下功能：

1. 给定时区为GMT+8从而完成了东八区时间的正常显示。也可以用getRawOffset来实现当前机型的时区设置。

2. 绘制刻度盘上的数字。这里参照了刻度盘刻度的绘制，按照半径再减去一个固定值作为基础位置，再算出当前数字的textBound宽度，来计算出偏移后的实际text起始绘制位置。
3. 指针的绘制，为绘制一条给定长度的线段后，依照计算出的时间对应的角度值来rotate，得出最终的结果（注意，由于在数学中的角度起始值在正右边，逆时针增加，故需要一系列数学转化成为合适的钟表角度）。此外，每秒跳动一次是由postInvalidateDelay来实现的，时针和分针的位置也是每秒变换一次的。
4. 圆心的绘制，是绘制2个同心圆。注意，在同一个canvas上，后绘制的物体会覆盖在前面之上，即新的图层永远是在顶层的。