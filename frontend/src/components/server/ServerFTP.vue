<template>
    <div class="ftp">
        <div class="flex flex-center mb1">
            <h4 class="flex-child">FTP Access</h4>
            <div>
                <button :class="{ 'disabled': editingFile === null && (path.length === 0) }" @click="back()"
                        class="btn mr1">Back
                </button>
                <button @click="list()" :class="editingFile !== null ? 'disabled' : ''" key="refresh" class="btn mr1">
                    Refresh
                </button>
                <transition name="fade" mode="out-in">
                    <button v-if="editingFile" :class="{ 'disabled': savingFile }" @click="saveFile" key="save"
                            class="btn info full ml1">
                        <span v-if="savingFile">Saving...</span>
                        <span v-else>Save file</span>
                    </button>
                    <router-link tag="button" :to="{ name: 'ftp-upload' }" v-if="editingFile === null" @click="list()"
                                 key="upload" class="btn info ml1">Upload files
                    </router-link>
                </transition>
            </div>
        </div>
        <transition name="fade">
            <router-view key="directory" v-if="editingFile === null" :fileList="fileList" :path="path"/>
            <ServerFTPFileEdit v-else key="file" v-on:fileChanged="updateEditingFile" :serverId="$attrs.serverId"
                               :file="editingFile" :path="path"/>
        </transition>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import ServerFTPFileList from "@/views/server/ServerFTPFileList.vue";
    import {UploadIcon} from "vue-feather-icons/icons";
    import ServerFTPFileEdit from "@/views/server/ServerFTPFileEdit.vue";

    @Component({
        components: {ServerFTPFileEdit, UploadIcon, ServerFTPFileList}
    })
    export default class ServerFTP extends Vue {
        editingFile: any = null;
        savingFile: boolean = false;

        fileList: Array<any> = [];
        path: Array<string> = [];

        created() {
            this.$bus.on("ftp-change-path", (path) => {
                this.path.push(path);
                this.list();
            });

            this.$bus.on("ftp-write-file", (file) => {
                this.editingFile = file;
            });

            this.list();
        }

        private updateEditingFile(content: string): void {
            this.editingFile.content = content;
        }

        private get relativePath(): string {
            return this.path.length === 0 ? "" : this.path.join("/");
        }

        private saveFile() {
            if (this.savingFile) return;

            this.savingFile = true;
            this.$http({
                method: "POST",
                // @ts-ignore
                url: "servers/" + this.$attrs.server.id + "/ftp?path=" + this.editingFile.relativePath,
                data: {
                    content: this.editingFile.content
                }
            }).then(() => {
                console.log("file-saved");
            }).catch((error: any) => {
                console.error(error);
            }).then(() => {
                this.savingFile = false;
            });
        }

        private back() {
            if (this.editingFile !== null) {
                this.editingFile = null;
                return;
            }

            this.path.pop();
            this.list();
        }

        private list() {
            this.$http({
                method: "GET",
                // @ts-ignore
                url: "servers/" + this.$attrs.server.id + "/ftp?path=" + this.relativePath,
            }).then((result: any) => {
                this.fileList = result.data.content;
            }).catch((error: any) => {
                console.error(error);
            });
        }
    }
</script>
