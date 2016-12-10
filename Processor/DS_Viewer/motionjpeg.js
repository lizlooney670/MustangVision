	
If the camera exposes raw JPEG images (not .MJPEG extension) you'll have to reaload it manually (if the extension is .MJPEG the browser will do everything, just put the correct src). If you have .MJPEG and want to use the raw .JPEG check your camera documentation. Most cameras expose both the .MJPEG and raw .JPEG streams (just on different URLs).

Unfortunately you won't be able to easily get the image through ajax, but you could change the src of the image periodically.

You can use Date.getTime() and add it to the querystring to force the browser to reload the image, and repeat each time the image loads.

If you use jQuery the code will look something like this:

camera.html

<!DOCTYPE html>
<html>

<head>
    <title>ipCam</title>
</head>

<body>
    <h1>ipCam</h1>
    <img id="motionjpeg" src="http://user:pass@127.0.0.1:8080/" />
    <script src="motionjpeg.js"></script>
    <script>
        //Using jQuery for simplicity

        $(document).ready(function() {
            motionjpeg("#motionjpeg"); // Use the function on the image
        });
    </script>
</body>

</html>
motionjpeg.js

function motionjpeg(id) {
    var image = $(id), src;

    if (!image.length) return;

    src = image.attr("src");
    if (src.indexOf("?") < 0) {
        image.attr("src", src + "?"); // must have querystring
    }

    image.on("load", function() {
        // this cause the load event to be called "recursively"
        this.src = this.src.replace(/\?[^\n]*$/, "?") +
            (new Date()).getTime(); // 'this' refers to the image
    });
}