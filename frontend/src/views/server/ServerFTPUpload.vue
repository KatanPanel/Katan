<template>
    <div class="ftp-upload">
        <h4 class="flex-child mb2">Uploading files</h4>
        <div class="box">
            <div class="flex">
                <form class="box box-dark upload-box tc flex mr1 flex-center flex-column flex-child" id="upload-form" enctype="multipart/form-data">
                    <DownloadIcon/>
                    <input style="display: none" multiple />
                    <div class="box-title mt2" style="font-weight: 500;">Click to <b>choose a file</b>.</div>
                </form>
                <div class="box box-dark flex-child ml1">
                    <div class="box-title tl">Target directory</div>
                    <form action="" class="form">
                        <div class="group">
                            <input type="text" class="input box box-dark" placeholder="">
                        </div>
                    </form>
                    <div class="box-title tl mt2">Restrictions</div>
                    <ul style="color: rgba(255, 255, 255, 0.8)" class="box box-dark">
                        <li>Max file size is 10MB.</li>
                        <li class="mt1">Hentai aren't permitted.</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {CoffeeIcon, FileIcon, FileTextIcon, FolderIcon, PlusIcon, DownloadIcon} from "vue-feather-icons/icons";

    @Component({
        components: {DownloadIcon, PlusIcon, CoffeeIcon, FileTextIcon, FileIcon, FolderIcon}
    })
    export default class ServerFTPUpload extends Vue {
        uploadingFiles: Array<any> = [];
        private uploadForm!: HTMLFormElement;

        get path() {
            return this.$attrs.path
        }

        get fileList() {
            return this.$attrs.fileList
        }

        private changePath(file: any) {
            if (file.directory)
                this.$bus.emit("ftp-change-path", file.name);
            else
                this.$bus.emit("ftp-write-file", file);
        }

        created() {
            this.$bus.emit("ftp-change-path", "/");
        }

        mounted() {
            this.uploadForm = <HTMLFormElement> document.getElementById("upload-form")!!;
            this.uploadForm.addEventListener("drag dragstart dragend dragover dragenter dragleave drop", e => {
                var drag = <DragEvent> e;
                console.log("DragEvent", drag)
            })
        }
    }
</script>
<style lang="scss" scoped>
    .upload-box {
        border: 4px dashed rgba(0, 0, 0, .1);
        border-radius: .4rem;
        padding: 1rem;
        cursor: pointer;


        &:hover {
            background-color: rgba(0, 0, 0, .1);
        }

        svg {
            opacity: .47;
            width: 72px;
            height: 72px;
        }
    }

    ul {
        list-style-type: none;

    }
</style>
