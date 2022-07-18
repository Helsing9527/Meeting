$(function () {
    new Vue({
        el: '#app',
        data() {
            return{
                // 初始页表单
                staffHiredForm: {
                    userName: '',
                    dept: '',
                    post: '',
                    gender: '',
                    imgBase64: '',
                    admin: true,
                    admin_code: ''
                },
                rulesStaffHired: {
                    userName: [
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
                    imgBase64: [
                        {required: true, message: '请拍照或上传照片', trigger: 'input'}
                    ],
                    admin_code: [
                        {required: true, message: '请输入管理员授权码', trigger: 'blur'}
                    ]
                },
                // 表单输入框宽度
                formLabelWidth: '120px',
                // 拍照弹窗
                photographDialogVisible: false,
                // 选择上传的照片列表
                fileList: [],
            }
        },
        methods: {
            // 提交表单
            submitStaffHiredForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid && this.staffHiredForm.admin_code == 'meeting') {
                        axios.post("/staff/hired", this.staffHiredForm).then((res) => {
                            if (res.data.flag) {
                                this.$message.success(res.data.msg);
                                this.resetForm('staffHiredForm');
                                this.fileList = [];
                                this.staffHiredDialogVisible = false;
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        }).finally(() => {
                            this.queryStaff();
                        })
                    } else {
                        this.$message.error('请校验表单后再提交 =_*');
                        return false;
                    }
                });
            },
            // 重置表单
            resetForm(formName) {
                this.$refs[formName].resetFields();
            },
            // 上传图片限制
            handleExceed(files, fileList) {
                this.$message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
            },

            // 覆盖默认的上传行为，可以自定义上传的实现
            httpRequest(data) {
                // 转base64
                this.getBase64(data.file).then(resBase64 => {
                    //直接拿到base64信息
                    this.staffHiredForm.imgBase64 = resBase64.replace(/^data:image\/\w+;base64,/, "");
                })
                var that = this
                setTimeout(function () {
                    that.uploadPercent = 100
                }, 2000)
            },

            // 转base64编码
            getBase64(file) {
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
                    //转结束就 resolve 出去
                    reader.onloadend = function () {
                        resolve(fileResult);
                    };
                });
            },
            // 拍照弹窗
            photoDialog() {
                this.photographDialogVisible = true;
                this.callCamera();
            },

            // 确定拍照
            photo() {
                var img = this.photograph();
                this.staffHiredForm.imgBase64 = img;
                this.photographDialogVisible = false;
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
                let jpegImg = this.$refs['canvas'].toDataURL('image/jpeg', 0.7);
                // 正则去掉base64头信息并返回值
                return jpegImg.replace(/^data:image\/\w+;base64,/, "");
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
        }
    })
})