$(function () {
    new Vue({
        el: "#app",
        data() {
            return {
                // 注册表单弹层
                registerDialogVisible: false,
                // 注册表单
                ruleForm: {
                    name: '',
                    dept: '',
                    post: '',
                    gender: '',
                },
                // 注册表单校验
                rules: {
                    name: [
                        {required: true, message: '请输入姓名', trigger: 'blur'},
                        {min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur'}
                    ],
                    dept: [
                        {required: true, message: '请选择部门', trigger: 'change'}
                    ],
                    post: [
                        {required: true, message: '请选择职位', trigger: 'change'}
                    ],
                    gender: [
                        {required: true, message: '请选择性别', trigger: 'change'}
                    ]
                }
            }
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
                    this.$message.error('摄像头开启失败，请检查摄像头是否可用！')
                })
            },
            // 拍照
            photograph() {
                let ctx = this.$refs['canvas'].getContext('2d')
                // 把当前视频帧内容渲染到canvas上
                ctx.drawImage(this.$refs['video'], 0, 0, 640, 480)
                // 转base64格式、图片格式转换、图片质量压缩---支持两种格式image/jpeg+image/png
                let imgBase64 = this.$refs['canvas'].toDataURL('image/jpeg', 0.7)

                /**------------到这里为止，就拿到了base64位置的地址，后面是下载功能----------*/

                // 由字节转换为KB 判断大小
                // let str = imgBase64.replace('data:image/jpeg;base64,', '')
                // let strLength = str.length
                // let fileLength = parseInt(strLength - (strLength / 8) * 2)　　　 // 图片尺寸  用于判断
                // let size = (fileLength / 1024).toFixed(2)
                // console.log(size) 　　  // 上传拍照信息  调用接口上传图片 .........
                //
                // // 保存到本地
                // let ADOM = document.createElement('a')
                // ADOM.href = this.headImgSrc
                // ADOM.download = new Date().getTime() + '.jpeg'
                // ADOM.click()
            },
            // 关闭摄像头
            closeCamera() {
                if (!this.$refs['video'].srcObject) return
                let stream = this.$refs['video'].srcObject
                let tracks = stream.getTracks()
                tracks.forEach(track => {
                    track.stop()
                })
                this.$refs['video'].srcObject = null
            },
            // 注册表单
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.$message.success('注册成功 ^_^');
                        this.registerDialogVisible = false;
                    } else {
                        this.$message.error('注册失败 :(')
                        return false;
                    }
                });
            },
            resetForm(formName) {
                this.$refs[formName].resetFields();
            }
        },
        mounted() {
            this.callCamera();
        },
        destroy() {
            this.closeCamera();
        }
    });

});