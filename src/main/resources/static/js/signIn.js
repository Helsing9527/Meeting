$(function () {
    new Vue({
        el: "#app",
        data() {
            return {}
        },
        methods: {
            // 调用摄像头
            callCamera() {
                // H5调用电脑摄像头API
                navigator.mediaDevices.getUserMedia({
                    video: true
                }).then(success => {
                    // 摄像头开启成功
                    this.$refs['video'].srcObject = success;
                    // 实时拍照效果
                    this.$refs['video'].play();
                }).catch(error => {
                    this.$message.error('摄像头开启失败，请检查摄像头是否可用！');
                })
            },
            // 拍照
            photograph() {
                let ctx = this.$refs['canvas'].getContext('2d');
                // 把当前视频帧内容渲染到canvas上
                ctx.drawImage(this.$refs['video'], 0, 0, 640, 480);
                // 转base64格式、图片格式转换、图片质量压缩---支持两种格式image/jpeg+image/png
                let imgBase64 = this.$refs['canvas'].toDataURL('image/jpeg', 0.7);
                console.log(imgBase64)
                axios.post("/meeting/signIn", {"imgBase64": imgBase64}).then((res) => {
                    if (res.data.flag) {
                        this.$message.success(res.data.msg);
                    } else {
                        this.$message.error(res.data.msg);
                    }
                })
            },
            // 关闭摄像头
            closeCamera() {
                if (!this.$refs['video'].srcObject) return;
                let stream = this.$refs['video'].srcObject;
                let tracks = stream.getTracks();
                tracks.forEach(track => {
                    track.stop();
                })
                this.$refs['video'].srcObject = null;
            }
        },
        created() {
            this.callCamera()
        }
    })
})