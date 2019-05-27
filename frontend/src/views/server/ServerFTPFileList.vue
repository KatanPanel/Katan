<template>
    <div>
        <div class="flex mb1">
            <small class="label" style="width: 100%; opacity: .7;">
                Path: <span class="ml1" style="font-weight: 500">~ {{ path.length === 0 ? "root" : path.join("/") }}</span>
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
        <div class="box br0" v-if="fileList.length === 0">
            <div class="box-title" style="opacity: 1">This folder is empty.</div>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";
    import {CoffeeIcon, FileIcon, FileTextIcon, FolderIcon} from "vue-feather-icons/icons";

    @Component({
        components: {CoffeeIcon, FileTextIcon, FileIcon, FolderIcon}
    })
    export default class ServerFTPFileList extends Vue {
        get path(): any {
            return this.$attrs.path;
        }

        get fileList(): any {
            return this.$attrs.fileList;
        }

        private changePath(file: any) {
            if (file.directory)
                this.$bus.emit("ftp-change-path", file.name);
            else
                this.$bus.emit("ftp-write-file", file);
        }

    }
</script>
