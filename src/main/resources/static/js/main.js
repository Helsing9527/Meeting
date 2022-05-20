$(function () {
    new Vue({
        el: "#app",
        data() {
            return {
                // 注册表单弹层
                registerDialogVisible: false,
                // 相机弹窗
                cameraDialogVisible: false,
                loginBase64: '',
                // 注册表单
                ruleForm: {
                    name: '',
                    dept: '',
                    post: '',
                    gender: '',
                    base64: '',
                    faceId: '',
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
                    ],
                    base64: [
                        {required: true, message: '请拍照或上传照片', trigger: 'input'}
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
                this.ruleForm.base64 = imgBase64;
                this.loginBase64 = imgBase64;
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
            },
            // 注册表单
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        console.log(this.ruleForm)
                        console.log(formName)
                        console.log(valid)
                        axios.post("/index", this.ruleForm).then((res) => {
                            if (res.data.flag) {
                                this.registerDialogVisible = false;
                                this.$message.success(res.data.msg);
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        })
                    } else {
                        this.$message.error('请完善表单内容 :(');
                        return false;
                    }
                });
            },
            // 调用摄像头弹窗
            cameraDialog() {
                this.callCamera();
                this.cameraDialogVisible = true;
            },
            // 拍照 Go!
            photo() {
                this.photograph();
                this.cameraDialogVisible = false;
                console.log(this.loginBase64);
            },
            // 登录按钮
            login() {
                this.photo();
                axios.post("/index/login", {"loginBase64": this.loginBase64}).then((res) => {
                    if (res.data.flag) {
                        this.$message.success(res.data.msg);
                    } else {
                        this.$message.error(res.data.msg);
                    }
                })
            },
            // 注册按钮
            register() {
                this.registerDialogVisible = true;
            },
            resetForm(formName) {
                this.$refs[formName].resetFields();
            },
            // 弹窗关闭事件
            closeDialog() {
                this.closeCamera();
            },

            // 覆盖默认的上传行为，可以自定义上传的实现
            httpRequest(data) {
                // 转base64
                this.getBase64(data.file).then(resBase64 => {
                    //直接拿到base64信息
                    this.fileBase64 = resBase64;
                    this.ruleForm.base64 = this.fileBase64;
                })
                var that = this
                setTimeout(function () {
                    that.uploadPercent = 100
                }, 2000)
            },
            // 转base64编码
            getBase64(file) {
                this.dialogVisible = false
                return new Promise((resolve, reject) => {
                    let reader = new FileReader();
                    let fileResult = "";
                    reader.readAsDataURL(file);
                    //开始转
                    reader.onload = function () {
                        fileResult = reader.result;
                    };
                    //转失败
                    reader.onerror = function (error) {
                        reject(error);
                    };
                    //转结束咱就 resolve 出去
                    reader.onloadend = function () {
                        resolve(fileResult);
                    };
                });
            }
        },
        created() {
            this.callCamera()
            console.log('启动摄像头')
        }
    });

});