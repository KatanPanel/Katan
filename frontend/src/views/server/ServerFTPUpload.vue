<template>
    <div class="ftp-upload">
        <h4 class="flex-child mb2">Uploading files</h4>
        <div class="box">
            <div class="flex">
                <div class="box box-dark flex-child mr1">
                    <div class="box-title tl">Target path</div>
                    <form action="" class="form">
                        <div class="group">
                            <input type="text" class="input" placeholder="/">
                        </div>
                    </form>
                    <div class="box-title tl mt2">Restrictions</div>
                    <ul style="list-style-type: none" class="box box-dark">
                        <li>Max file size is 10MB</li>
                    </ul>
                </div>
                <div class="box box-dark flex-child ml1">
                    <div class="box-title">Put your files here</div>
                    <div class="flex flex-row flex-wrap flex-center">
                        <div class="upload-box" v-for="i in 5">
                            <PlusIcon/>
                        </div>
                    </div>
                    <div class="box-title mt2">
                        Progress<br/>
                        <span style="text-transform: none; font-weight: 500;">Uploaded 0 of 0 files</span>
                    </div>
                    <progress value="30" max="100"></progress>
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    //@ts-ignore
    import {CoffeeIcon, FileIcon, FileTextIcon, FolderIcon, PlusIcon} from "vue-feather-icons/icons/index";

    @Component({
        components: {PlusIcon, CoffeeIcon, FileTextIcon, FileIcon, FolderIcon}
    })
    export default class ServerFTPUpload extends Vue {
        get path() {
            return this.$attrs.path
        }

        get fileList() {
            return this.$attrs.fileList
        }

        created() {
            this.$bus.emit("ftp-change-path", "/");
        }

        private changePath(file: any) {
            if (file.directory)
                this.$bus.emit("ftp-change-path", file.name);
            else
                this.$bus.emit("ftp-write-file", file);
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

        &:not(:last-child) {
            margin-right: 1rem;
        }

        svg {
            opacity: .47;
            width: 64px;
            height: 64px;
        }
    }
</style>
