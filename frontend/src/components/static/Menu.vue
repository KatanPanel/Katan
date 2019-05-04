<template>
  <div class="menu">
    <div class="klogo">K</div>
    <ul>
      <router-link tag="li" :to="{ name: 'server' }">
        <a href="#">
          <GridIcon />
          <span>Home</span>
        </a>
      </router-link>
      <router-link tag="li" :to="{ name: 'console' }">
        <a href="#">
          <TerminalIcon />
          <span>Console</span>
        </a>
      </router-link>
      <li>
        <a href="#">
          <FolderIcon />
          <span>FTP Access</span>
        </a>
      </li>
      <li>
        <a href="#">
          <SettingsIcon />
          <span>Settings</span>
        </a>
      </li>
      <li>
        <a href="#">
          <DownloadCloudIcon />
          <span>Backup</span>
        </a>
      </li>
      <li>
        <a href="#">
          <HelpCircleIcon />
          <span>Help</span>
        </a>
      </li>
    </ul>
  </div>
</template>
<script lang="ts">
// @ts-ignore
import {
  HelpCircleIcon,
  GridIcon,
  TerminalIcon,
  FolderIcon,
  HomeIcon,
  SettingsIcon,
  LogOutIcon,
  DownloadCloudIcon
} from "vue-feather-icons/icons";
import { Component, Vue } from "vue-property-decorator";

@Component({
  components: {
    HelpCircleIcon,
    GridIcon,
    DownloadCloudIcon,
    FolderIcon,
    LogOutIcon,
    SettingsIcon,
    TerminalIcon,
    HomeIcon
  }
})
export default class Menu extends Vue {}
</script>
