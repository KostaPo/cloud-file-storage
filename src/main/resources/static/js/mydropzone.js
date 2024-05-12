Dropzone.autoDiscover = false;
document.getElementById("startUpload").style.display = "none";
document.getElementById("clearQueue").style.display = "none";
var previewTemplate = document.querySelector('#preview_template').innerHTML;

var myDropzone = new Dropzone("#myDropzone", {
    url: "/upload",
    autoProcessQueue: false,
    uploadMultiple: true,
    maxFilesize: 1000,
    parallelUploads: 100,
    dictCancelUpload: "Остановить загрузку",
    dictUploadCanceled: "Загрузка остановлена",
    dictRemoveFile: "Удалить",
    dictFileTooBig: "Размер файла больше {{maxFilesize}} мб!",
    previewsContainer: ".dropzone-previews",
    previewTemplate: previewTemplate
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

myDropzone.on("sending", function (file, xhr, formData) {
    //TODO в xhr передавать токены
    formData.append("files", file, file.fullPath);
});

myDropzone.on("addedfile", function () {
    //показываем кнопки если есть файлы к отправке
    document.getElementById("startUpload").style.display = "block";
    document.getElementById("clearQueue").style.display = "block";
});

myDropzone.on("removedfile", function () {
    //скрываем кнопки если нет файлов к отправке
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