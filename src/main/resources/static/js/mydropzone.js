Dropzone.autoDiscover = false;
document.getElementById("startUpload").style.display = "none";
document.getElementById("clearQueue").style.display = "none";
var previewTemplate = document.querySelector('#preview_template').innerHTML;

var myDropzone = new Dropzone("#myDropzone", {
    url: "/upload",
    paramName: function() {return "data";},
    uploadMultiple: true,
    autoProcessQueue: false,
    maxFilesize: 1024,
    maxFiles: 100,
    parallelUploads: 100,
    dictFileTooBig: "Размер файла больше {{maxFilesize}} мб!",
    previewsContainer: ".dropzone-previews",
    previewTemplate: previewTemplate
});

myDropzone.on("sendingmultiple", function(files, xhr, formData) {
    formData.append('data', JSON.stringify(files));
});

myDropzone.on("success", function (file) {
    this.removeFile(file);
});

myDropzone.on("error", function (file, message, xhr) {
    file.previewElement.querySelector('.dz-error-message').innerText = xhr && xhr.response
        ? JSON.parse(xhr.response).message
        : message;
});

myDropzone.on("uploadprogress", function (file, progress, bytesSent) {
    file.previewElement.querySelector('.dz-progress .dz-upload').style.width = progress + "%";
});

myDropzone.on("addedfile", function () {
    document.getElementById("startUpload").style.display = "block";
    document.getElementById("clearQueue").style.display = "block";
});

myDropzone.on("removedfile", function () {
    if (myDropzone.getQueuedFiles().length === 0) {
        document.getElementById("startUpload").style.display = "none";
        document.getElementById("clearQueue").style.display = "none";
    }
});

document.getElementById('startUpload').addEventListener('click', function () {
    myDropzone.processQueue();
});
document.getElementById('clearQueue').addEventListener('click', function () {
    myDropzone.removeAllFiles();
});