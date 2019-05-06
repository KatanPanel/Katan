<template>
    <div class="file-ftp">
        <div class="label mb1" style="opacity: .7;">Editing: <span class="ml1"
                                                                   style="font-weight: 500">{{ file.name }}</span></div>
        <div class="form">
            <textarea>{{ content }}</textarea>
        </div>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    @Component
    export default class ServerFileFTP extends Vue {
        content: string = "";

        get file() {
            return this.$attrs.file;
        }

        created() {
            this.$http({
                method: "GET",
                // @ts-ignore
                url: "server/" + this.$attrs.server + "/ftp/file?path=" + this.file.name,
            }).then((result: any) => {
                this.content = result.data.message;
            }).catch((error: any) => {
                console.error(error);
            });
        }
    }
</script>
