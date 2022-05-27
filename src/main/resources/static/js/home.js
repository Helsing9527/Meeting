$(function () {
    new Vue({
        el: "#app",
        data() {
            return {
                // 默认显示欢迎页
                index: '1',
                // 欢迎页日历
                value: new Date(),
                // 用户头像
                url: 'https://cn.vuejs.org/images/logo.svg',
                // 会议申请 人员表格
                tableData: [],
                // 会议申请 分页工具条
                pagination: {//分页相关模型数据
                    currentPage: 1,//当前页码
                    pageSize: 10,//每页显示的记录数
                    total: 0,//总记录数
                    name: '',//姓名
                    dept: '',//部门
                    post: '',//职位
                    gender: ''//性别
                },
                // 人员确认后临时存储
                multipleSelection: [],
                // 会议申请 表单/表单校验
                ruleForm: {
                    meetingName: '',//会议名称
                    meetingPlace: '',//会议地点
                    meetingTime: '',//会议日期
                    meetingDesc: '',//会议内容
                    persons: '',//选中的参会的人员
                    status: ''//默认状态为未开始
                },
                rules: {
                    meetingName: [
                        {required: true, message: '请输入会议名称', trigger: 'blur'},
                        {min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur'}
                    ],
                    meetingPlace: [
                        {required: true, message: '请选择会议室', trigger: 'change'}
                    ],
                    meetingTime: [
                        {type: 'date', required: true, message: '请选择日期', trigger: 'change'}
                    ],
                    meetingDesc: [
                        {required: true, message: '请填写会议内容', trigger: 'blur'}
                    ],
                    persons: [
                        {required: true, message: '请选择参会人员', trigger: 'input'}
                    ]
                },
                //选择会议人员对话框
                personsDialogVisible: false,
                // 会议列表
                meetingList: [],
                // 会议列表 编辑
                editDialogVisible: false,
                // 人员库 创建
                createGroupRuleForm: {
                    groupName: '',
                    groupId: '',
                    remark: ''
                },
                createGroupRules: {
                    groupName: [
                        {required: true, message: '请输入人员库名称', trigger: 'blur'},
                        {min: 1, max: 60, message: '长度在 1 到 60 个字符', trigger: 'blur'}
                    ],
                    groupId: [
                        {required: true, message: '请输入人员库 ID', trigger: 'blur'}
                    ]
                },
                // 创建人员 表单/校验
                personForm: {
                    name: '',
                    dept: '',
                    post: '',
                    gender: '',
                    base64: '',
                    faceId: '',
                    adminCode: ''
                },
                personFormRules: {
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
                },
                // 人员库
                groupTableData: [],
                // 人员编辑弹窗
                personDialogVisible: false,
                // 相机弹窗
                cameraDialogVisible: false,
                // 签到位图
                loginBase64: ''
            };
        },
        methods: {
            // 导航二级菜单
            handleOpen(key, keyPath) {
                // console.log(key, keyPath);
            },
            handleClose(key, keyPath) {
                // console.log(key, keyPath);
            },
            // 导航内容切换
            setIndex(val) {
                this.index = val;
            },
            // 会议申请 人员列表
            selectPersons() {
                this.getAll();
                this.personsDialogVisible = true;
            },
            // 分页查询数据库人员并回填列表
            getAll() {
                axios.get("/meeting/" + this.pagination.currentPage + "/" + this.pagination.pageSize).then((res) => {
                    var records = res.data.data.records;
                    for (let record of records) {
                        if (record.gender == 1) {
                            record.gender = '男'
                        } else {
                            record.gender = '女'
                        }
                    }
                    this.tableData = res.data.data.records;
                    this.pagination.currentPage = res.data.data.current;
                    this.pagination.pageSize = res.data.data.size;
                    this.pagination.total = res.data.data.total;
                })
            },
            // 变色表格
            tableRowClassName({row, rowIndex}) {
                if (rowIndex % 3 == 1) {
                    return 'warning-row';
                } else if (rowIndex % 3 != 2) {
                    return 'success-row';
                }
                return '';
            },
            // 人员列表选择人员
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            // 人员列表选中确认后遍历获取所有id，并赋值给表单等待提交
            selectPerson() {
                var str = [];
                var val = this.multipleSelection;
                for (let i = 0; i < val.length; i++) {
                    str.push(val[i].id);
                }
                this.ruleForm.persons = str.toString();
                this.personsDialogVisible = false;
            },
            // 分页
            handleSizeChange(val) {
                this.pagination.pageSize = val;
                this.getAll();
            },
            handleCurrentChange(val) {
                this.pagination.currentPage = val;
                this.getAll();
            },
            // 会议申请
            meetingApply() {
                this.setIndex(2);
                this.ruleForm = {}
            },
            // 会议申请表单
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        axios.post("/meeting", this.ruleForm).then((res) => {
                            if (res.data.flag) {
                                this.resetForm('ruleForm');
                                this.$message.success(res.data.msg);
                                this.meetingManagerList();
                            } else {
                                this.$message.error(res.data.msg);
                            }
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
            // 会议列表
            meetingManagerList() {
                this.index = 3;
                this.meetingTable();
            },
            // 会议列表 查询数据
            meetingTable() {
                axios.get("/meeting").then((res) => {
                    let data = res.data.data;
                    // 遍历出每一次会议
                    for (let datum of data) {
                        // 格式化时间为中国标准时间
                        let date = new Date(datum.meetingTime);
                        datum.meetingTime = date.toString();
                        let personName = [];
                        // 发送此次会议所有 id 返回 所有id 人员信息
                        axios.get("/meeting/ids/" + datum.persons).then((res) => {
                            let users = res.data.data;
                            for (let user of users) {
                                // 此次会议中每个人的姓名
                                personName.push(user.name + " ")
                            }
                        })
                        datum.persons = personName;
                    }
                    this.meetingList = res.data.data;
                })
            },
            // 会议列表 编辑
            handleEdit(row) {
                axios.get("/meeting/" + row.id).then((res) => {
                    if (res.data.flag && res.data.data != null) {
                        this.editDialogVisible = true;
                        this.ruleForm = res.data.data;
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    this.meetingTable();
                })
            },
            // 会议列表 删除
            handleDelete(row) {
                this.$confirm("确定要删除吗？", "提示", {type: "info",}).then(() => {
                    axios.delete("/meeting/" + row.id).then((res) => {
                        if (res.data.flag) {
                            this.$message.success(res.data.msg);
                        } else {
                            this.$message.error(res.data.msg);
                        }
                    }).finally(() => {
                        this.meetingTable();
                    })
                }).catch(() => {
                    this.$message.info("取消操作")
                })
            },
            // 会议列表 修改
            updateForm() {
                axios.put("/meeting", this.ruleForm).then((res) => {
                    if (res.data.flag) {
                        this.editDialogVisible = false;
                        this.$message.success(res.data.msg);
                        this.resetForm('ruleForm');
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    this.meetingTable();
                })
            },
            // 人员库创建
            createGroup() {
                this.setIndex(4);
            },
            // 人员库创建表单
            submitCreateGroupForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        axios.post("/meeting/group", this.createGroupRuleForm).then((res) => {
                            if (res.data.flag) {
                                this.resetForm('createGroupRuleForm')
                                this.$message.success(res.data.msg);
                                this.group();
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        })
                    } else {
                        this.$message.error('请校验表单后再提交 =_*');
                        return false;
                    }
                });

            },
            // 人员库列表
            group() {
                this.setIndex(5);
                axios.get("/meeting/group").then((res) => {
                    // 传回的data数据为String,先转换为json对象
                    var parseData = JSON.parse(res.data.data);
                    this.groupTableData = parseData.GroupInfos;
                })
            },
            // 人员库删除
            groupHandleDelete(row) {
                this.$confirm("删除该人员库及包含的所有的人员，确定要删除吗？", "警告", {type: "info",}).then(() => {
                    axios.delete("/meeting/group/" + row.GroupId).then((res) => {
                        if (res.data.flag) {
                            this.$message.success(res.data.msg);
                        } else {
                            this.$message.error(res.data.msg);
                        }
                    }).finally(() => {
                        this.group();
                    })
                }).catch(() => {
                    this.$message.info("取消操作")
                })
            },
            // 创建人员
            createPerson() {
                this.setIndex(6);
                this.ruleForm = {}
            },
            // 覆盖默认的上传行为，可以自定义上传的实现
            httpRequest(data) {
                // 转base64
                this.getBase64(data.file).then(resBase64 => {
                    //直接拿到base64信息
                    this.fileBase64 = resBase64;
                    this.personForm.base64 = this.fileBase64;
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
                    //转结束咱就 resolve 出去
                    reader.onloadend = function () {
                        resolve(fileResult);
                    };
                });
            },
            // 创建人员 注册
            registerForm(formName) {
                console.log(this.personForm)
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        axios.post("/index", this.personForm).then((res) => {
                            if (res.data.flag) {
                                this.resetForm('personForm');
                                this.$message.success(res.data.msg);
                                this.persons();
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
            // 人员列表
            persons() {
                this.setIndex(7);
                this.getAll();
            },
            // 人员修改 弹窗
            personHandleEdit(row) {
                axios.get("/meeting/person/" + row.id).then((res) => {
                    if (res.data.flag && res.data.data != null) {
                        this.personDialogVisible = true;
                        this.personForm = res.data.data;
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    this.persons();
                })
            },
            // 人员修改
            personHandleUpdate() {
                axios.put("/meeting/person", this.personForm).then((res) => {
                    if (res.data.flag) {
                        this.personDialogVisible = false;
                        this.$message.success(res.data.msg);
                        this.personForm = {}
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    this.persons();
                })
            },
            // 人员删除
            personHandleDelete(row) {
                this.$confirm("确定要删除吗？", "警告", {type: "info",}).then(() => {
                    axios.delete("/meeting/person/" + row.id).then((res) => {
                        if (res.data.flag) {
                            this.$message.success(res.data.msg);
                        } else {
                            this.$message.error(res.data.msg);
                        }
                    }).finally(() => {
                        this.persons();
                    })
                }).catch(() => {
                    this.$message.info("取消操作")
                })
            },
            // 开始会议
            startMeeting() {
                axios.get("/meeting/person/" + $("#userId").text()).then((res) => {
                    if (res.data.data.status == 1) {
                        this.callCamera();
                        this.cameraDialogVisible = true;
                        this.$message.success("签到完成");
                    } else {
                        this.$message.error("当前没有会议");
                    }
                })
            },
            // 拍照 Go!
            photo() {
                this.photograph();
                this.cameraDialogVisible = false;
                this.closeCamera();
            },
            // 拍照
            photograph() {
                let ctx = this.$refs['canvas'].getContext('2d');
                // 把当前视频帧内容渲染到canvas上
                ctx.drawImage(this.$refs['video'], 0, 0, 640, 480);
                // 转base64格式、图片格式转换、图片质量压缩---支持两种格式image/jpeg+image/png
                let imgBase64 = this.$refs['canvas'].toDataURL('image/jpeg', 0.7);
                this.loginBase64 = imgBase64;
                console.log(imgBase64)
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
    });
})