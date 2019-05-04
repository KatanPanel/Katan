<template>
    <div>
        <h4 class="mb1">Resource usage</h4>
        <div class="flex flex-row mb1">
            <div class="flex-child mr1">
                <div class="box box-icon flex flex-row">
                    <div class="icon"><LayersIcon/></div>
                    <div class="flex flex-center flex-child">
                        <div class="title flex-child">Memory</div>
                        <div class="value">448MB / {{ getServerMemory }}MB</div>
                    </div>
                </div>
            </div>
            <div class="flex-child mr1">
                <div class="box box-icon flex flex-row">
                    <div class="icon"><HardDriveIcon/></div>
                    <div class="flex flex-center flex-child">
                        <div class="title flex-child">Disk</div>
                        <div class="value">1.5GB / Unlimited</div>
                    </div>
                </div>
            </div>
            <div class="flex-child">
                <div class="box box-icon flex flex-row">
                    <div class="icon"><CpuIcon/></div>
                    <div class="flex flex-center flex-child">
                        <div class="title flex-child">CPU</div>
                        <div class="value">4 threads active</div>
                    </div>
                </div>
            </div>
        </div>
        <div class="flex flex-row">
            <div class="box box-dark mr1" style="width: 49%">
                <p class="box-title">CPU</p>
                <div class="progress-container">
                    <progress value="24" max="100"></progress>
                    <span>24%</span>
                </div>
                <canvas class="mt2" id="cpu-chart"></canvas>
            </div>
            <div class="box box-dark" style="width: 50%">
                <p class="box-title">Memory</p>
                <div class="progress-container">
                    <progress value="77" max="100"></progress>
                    <span>77%</span>
                </div>
                <canvas class="mt2" id="memory-chart"></canvas>
            </div>
        </div>
    </div>
</template>
<script lang="ts">
    import {Component, Vue} from "vue-property-decorator";

    // @ts-ignore
    import Chart from "chart.js";

    // @ts-ignore
    import {CpuIcon, LayersIcon, HardDriveIcon} from "vue-feather-icons/icons";

    @Component({
        components: {HardDriveIcon, LayersIcon, CpuIcon}
    })
    export default class ServerIndex extends Vue {
        private cpuChart!: HTMLCanvasElement;
        private memoryChart!: HTMLCanvasElement;

        private loadGraph() {
            Chart.defaults.global.defaultFontColor = "rgba(255, 255, 255, .47)";
            Chart.defaults.global.defaultFontFamily = "'Raleway', sans-serif";

            new Chart(this.cpuChart.getContext('2d'), {
                type: 'line',
                data: {
                    labels: ["18:05", "18:06", "18:07", "18:08", "18:09", "18:10"],
                    datasets: [{
                        data: [8, 12, 15, 11, 7, 13],
                        borderWidth: 1,
                        borderColor: "rgba(84, 160, 255, 1)",
                        backgroundColor: "rgba(84, 160, 255, .2)",
                    }]
                },
                options: {
                    legend: {display: false},
                    title: {display: false},
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                },
            });

            new Chart(this.memoryChart.getContext('2d'), {
                type: 'line',
                data: {
                    labels: ["18:05", "18:06", "18:07", "18:08", "18:09", "18:10"],
                    datasets: [{
                        data: [12, 19, 3, 5, 2, 3],
                        backgroundColor: 'rgba(84, 160, 255, .2)',
                        borderColor: 'rgba(84, 160, 255,1.0)',
                        borderWidth: 1
                    }]
                },
                options: {
                    legend: {
                        display: false
                    },
                    title: {
                        display: false
                    },
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                }
            });
        }

        mounted() {
            this.cpuChart = <HTMLCanvasElement>document.getElementById("cpu-chart")!!;
            this.memoryChart = <HTMLCanvasElement>document.getElementById("memory-chart")!!;
            this.loadGraph();
        }
    }
</script>
<style lang="scss">
    .box {
        &.box-dark { background-color: rgba(0, 0, 0, .1) }
        &.box-icon {
            padding: 1rem 0 !important;
            color: rgba(255, 255, 255, .8);

            .icon {
                padding: 1rem;
                svg {
                    width: 34px;
                    height: 34px;
                    vertical-align: middle;
                }
            }

            .title {
                font-size: 20px;
                font-weight: 500;
            }

            .value {
                background-color: rgba(0, 0, 0, .1);
                padding: 5px 12px;
                border-radius: .4rem;
                margin-right: 1rem;
            }
        }
    }
</style>
