$(function () {
    new Vue({
        el: "#app",
        data() {
            return {}
        },
        methods: {
            // 登录
            login() {
                var imgBase64 = this.photograph();
                axios.post("/login", {"imgBase64": imgBase64}).then((res) => {
                    if (res.data.flag) {
                        // 登录成功
                        localStorage.setItem("staffInfo", res.data.data)
                        this.$message.success(res.data.msg);
                        window.location.href = '/workstation'
                    } else {
                        // 登录失败
                        this.$message.error(res.data.msg)
                    }
                })
            },
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
                let dataImg = this.$refs['canvas'].toDataURL('image/jpeg', 0.7);
                // 去掉base64头
                return dataImg.replace(/^data:image\/\w+;base64,/, "");
            }
        },
        created() {
            this.callCamera()
        }
    })
})