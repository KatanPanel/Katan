<template>
    <div>
        <div class="flex mb1">
            <small class="label" style="width: 100%; opacity: .7;">Path: <span class="ml1" style="font-weight: 500">~ {{ $utils.removeLeadingSlashes(path === "" || path === "/" ? "root" : path) }}</span>
            </small>
        </div>
        <table style="max-height: 320px">
            <thead>
            <tr>
                <th>File name</th>
                <th>Size</th>
                <th>Last modified</th>
            </tr>
            </thead>
            <tbody>
            <tr style="cursor: pointer" @click="changePath(file)" v-for="file in fileList" :key="file.name">
                <td>
                    <FolderIcon v-if="file.directory"/>
                    <CoffeeIcon v-else-if="file.extension === 'jar'"/>
                    <FileTextIcon v-else/>
                    <a href="#">{{ file.name }}</a>
                </td>
                <td>{{ $utils.formatBytes(file.length) }}</td>
                <td>{{ $utils.formatDate(file.lastModified) }}</td>
            </tr>
            </tbody>
        </table>
        <div class="box" v-if="fileList.length === 0">
            <div class="box-title" style="opacity: 1">This folder is empty.</div>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    //@ts-ignore
    import {CoffeeIcon, FileIcon, FileTextIcon, FolderIcon} from "vue-feather-icons/icons/index";

    @Component({
        components: {CoffeeIcon, FileTextIcon, FileIcon, FolderIcon}
    })
    export default class ServerFTPFileList extends Vue {
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
