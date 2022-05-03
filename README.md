# PDF Editor

<b>OpenJDK version:</b> "11.0.8" 2020-07-14

<b>Android SDK:</b> Android 10(API 29)

<b>Gradle version:</b> "6.6.1"

<b>PDF Reader:</b>

The PDF Reader displays a pdf of 55 pages. The title bar displays the name of the pdf document. The Status bar represents the page number out of the number of pages.
There are 6 tools for you to use on the top-right corner, their description is as follows:
- Pen Tool: A thin pen with which you can annotate on the pdf by drawing or scribbling. The pen color is blue.
- Highlighter Tool: A highlighter which is yellow in color with which you can annotate on the pdf by highlighting on it.
- Eraser Tool: The eraser will let you erase the annotations by pen and highlighter on the pdf.
- Undo: This tool lets you undo till the first action you performed.
- Redo: This tool lets you redo the action on which you performed undo.
- Cursor: This tool lets you get out of annotations or erase mode, so that on touch no annotations or erase action will be performed.

<b>How to go to next page or previous page:</b>

- There are two buttons on the bottom right corner of the screen. The button with the up arrow image is used to go to the previous page, and the button with the down arrow key is used to go to the next page.
- If you are on the first page, the button with up arrow key will be disabled.
- If you are on the last page, the down arrow key button will be disabled.

<b>Undo and Redo:</b>

- Every page in the pdf will have their own separate undo and redo stacks.
- You'll be able to perform undo and redo only on the actions performed in the current page on which you are on. If you want to undo or redo something on another page, you will first have to go to that page, and then click undo and redo. So, for instance you performed an action on Page 1, and then you move to Page 2 and you want to undo the action you performed on page 1, for this you will have to go back to page 1 and then click on undo, because when you are on page 2, you can undo or redo the actions performed only on page 2.
- If you performed no action then clicking on undo will not do anything.
- If you click on undo, but then you perform some action, then the redo button will not do anything. Redo works only when you first do undo, and then you do redo. If after undo you perform an action other than redo, then after that redo button will not do anything(since the redoStack becomes empty in this case.).
