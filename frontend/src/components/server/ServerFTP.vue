<template>
    <div class="ftp">
        <div class="flex flex-center mb1">
            <h4 class="flex-child">FTP Access</h4>
            <div>
                <button :class="path === '' || path === '/' ? 'disabled' : ''" @click="back()" class="btn mr1">Back
                </button>
                <button @click="list()" :class="editingFile !== null ? 'disabled' : ''" key="refresh" class="btn mr1">
                    Refresh
                </button>
                <transition name="fade" mode="out-in">
                    <button v-if="editingFile !== null" @click="saveFile" key="save" class="btn info full ml1">Save
                        file
                    </button>
                    <router-link tag="button" :to="{ name: 'ftp-upload' }" v-if="editingFile === null" @click="list()"
                                 key="upload" class="btn info ml1">Upload files
                    </router-link>
                </transition>
            </div>
        </div>
        <router-view :fileList="fileList" :path="path" :server="$attrs.server.id"/>
        <!-- <ServerFileFTP v-if="editingFile !== null" :file="editingFile" :server="$attrs.server.id"/> -->
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import ServerFTPFileList from "@/views/server/ServerFTPFileList.vue";
    import UploadIcon from "vue-feather-icons/icons/UploadIcon";
    import ServerFileFTP from "@/views/server/ServerFileFTP.vue";

    @Component({
        components: {ServerFileFTP, UploadIcon, ServerFTPFileList}
    })
    export default class ServerFTP extends Vue {
        uploading: boolean = false;
        editingFile: any = null;

        fileList: Array<any> = [];
        path: string = "/";

        created() {
            this.$bus.on("ftp-change-path", (path) => {
                this.path = this.$utils.removeLeadingSlashes(this.path + "/" + path);
                this.list();
            });

            this.$bus.on("ftp-write-file", (file) => {
                this.editingFile = file;
            })
        }

        private back() {
            this.path = this.path.substring(0, this.path.lastIndexOf("/"));
            this.editingFile = null;
            this.list();
        }

        private list() {
            this.$http({
                method: "GET",
                // @ts-ignore
                url: "server/" + this.$attrs.server.id + "/ftp?path=" + this.path,
            }).then((result: any) => {
                this.fileList = result.data.message;
            }).catch((error: any) => {
                console.error(error);
            });
        }
    }
</script>
