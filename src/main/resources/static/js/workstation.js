$(function () {
    new Vue({
        el: "#app",
        data() {
            return {
                // 默认 我的会议
                index: '1',
                // 登录用户信息
                staffInfo: {},
                // 默认头像
                url: 'https://cn.vuejs.org/images/logo.svg',

                /**
                 * 我的会议
                 */
                // 我创建的会议列表
                myCreatedMeeting: [],

                // 开始我的会议弹窗
                startMyMeetingDialogVisible: false,

                // 开始会议 暂存信息
                startMyMeetingInfo: {},

                // 我参与的会议列表
                myParticipantsMeeting: [],

                /**
                 * 会议管理
                 */
                // 会议列表
                meetingTable: [],

                // 参会人员/签到详情 弹窗
                checkInDialogVisible: false,

                // 参会人员签到统计
                checkInCount: {
                    total: '',
                    checkIn: '',
                    notCheckIn: ''
                },

                // 按条件查询
                meetingCondition: {
                    meetingName: '',
                    meetingRoom: '',
                    sponsor: ''
                },

                // 创建会议 弹窗
                createMeetingDialogVisible: false,
                // 创建会议 弹窗表单
                createMeetingForm: {
                    meetingName: '',
                    meetingRoom: '',
                    startingTime: '',
                    endTime: '',
                    meetingDesc: '',
                    participants: '',
                    sponsor: '',
                    sponsorId: '',
                    status: ''
                },
                rulesCreateMeeting: {
                    meetingName: [
                        {required: true, message: '请输入会议名称', trigger: 'blur'},
                        {min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur'}
                    ],
                    meetingRoom: [
                        {required: true, message: '请选择会议室', trigger: 'change'}
                    ],
                    startingTime: [
                        {required: true, message: '请选择开始时间', trigger: 'change'}
                    ],
                    endTime: [
                        {required: true, message: '请选择结束时间', trigger: 'change'}
                    ],
                    meetingDesc: [
                        {required: true, message: '请填写会议概述', trigger: 'blur'}
                    ],
                    participants: [
                        {required: true, message: '请选择参会人员', trigger: 'input'}
                    ]
                },
                // 创建会议 参会人员 弹窗
                selectStaffDialogVisible: false,
                // 创建会议 参会人员选择临时储存
                multipleSelection: [],

                // 当前选中会议行
                currentMeetingRow: '',

                // 编辑会议 弹窗
                editMeetingDialogVisible: false,

                // 开始会议 弹窗
                startMeetingDialogVisible: false,

                // 会议代签 弹窗
                helpCheckInDialogVisible: false,

                // 会议代签 列表
                helpCheckInTableDate: [],

                // 删除会议 弹窗
                deleteMeetingDialogVisible: false,

                /**
                 * 人员管理
                 */
                // 人员列表
                staffTable: [],

                // 按条件查询
                staffCondition: {
                    userName: '',
                    dept: '',
                    post: ''
                },

                // 员工入职 弹窗
                staffHiredDialogVisible: false,
                // 员工入职 弹窗表单
                staffHiredForm: {
                    userName: '',
                    dept: '',
                    post: '',
                    gender: '',
                    imgBase64: '',
                    admin: false
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
                    ]
                },
                // 表单输入框宽度
                formLabelWidth: '120px',
                // 选择上传的照片列表
                fileList: [],
                // 拍照弹窗
                photographDialogVisible: false,

                // 当前选中行
                currentStaffRow: '',
                // 员工信息修改弹窗
                staffEditDialogVisible: false,
                // 员工信息修改表单
                staffEditForm: {
                    userName: '',
                    dept: '',
                    post: '',
                    gender: '',
                    admin: false
                },
                rulesStaffEdit: {
                    dept: [
                        {required: true, message: '请选择部门', trigger: 'change'}
                    ],
                    post: [
                        {required: true, message: '请选择职位', trigger: 'change'}
                    ]
                },
                // 员工离职弹窗
                staffDimissionDialogVisible: false,

                /**
                 * 公用模型
                 */
                // 进入签到页面弹窗
                displayCheckInPageDialogVisible: false,

                // 分页工具条
                pagination: {
                    currentPage: 1,
                    pageSize: 10,
                    total: 0
                },
            }
        },
        methods: {
            /**
             * 导航栏
             */
            // 我的会议
            myMeeting() {
                this.index = '1'
                this.queryMyCreatedMeeting();
                this.queryMyParticipantsMeeting();
            },

            // 会议管理
            meetingManagement() {
                this.index = '2'
                this.queryMeeting();
                this.restPagination();
            },

            // 人员管理
            staffManagement() {
                this.index = '3';
                this.queryStaff();
                this.restPagination();
            },

            // 退出
            exit() {
                axios.get("/meeting/exit").then((res) => {
                    if (res.data.flag) {
                        localStorage.removeItem("staffInfo");
                        window.location.href = '/'
                    }
                })
            },

            /**
             * 我的会议
             */
            // 我发起的会议 列表
            queryMyCreatedMeeting() {
                axios.get("/meeting/myCreatedMeeting/" + this.staffInfo.id).then((res) => {
                    this.myCreatedMeeting = res.data.data;
                })
            },

            // 开始会议弹窗
            startMyMeeting(row) {
                if (row.sponsorId == this.staffInfo.id) {
                    this.startMyMeetingDialogVisible = true;
                    this.startMyMeetingInfo = row;
                } else {
                    this.$message.error('你没有权限开始会议 -_-||')
                }
            },

            // 开始会议
            confirmStartMyMeeting() {
                axios.put("/meeting/start", this.startMyMeetingInfo).then((res) => {
                    if (res.data.flag) {
                        this.$message.success(res.data.msg);
                        this.displayCheckInPageDialogVisible = true;
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    this.startMyMeetingDialogVisible = false;
                    this.queryMyCreatedMeeting();
                })
            },

            // 我参与的会议 列表
            queryMyParticipantsMeeting() {
                axios.get("/meeting/myParticipantsMeeting/" + this.staffInfo.id).then((res) => {
                    this.myParticipantsMeeting = res.data.data;
                })
            },

            /**
             * 会议管理
             */
            // 会议列表
            queryMeeting() {
                let param = "?meetingName=" + this.meetingCondition.meetingName;
                param += "&meetingRoom=" + this.meetingCondition.meetingRoom;
                param += "&sponsor=" + this.meetingCondition.sponsor;
                axios.get("/meeting/list/" + this.pagination.currentPage + "/" + this.pagination.pageSize + param).then((res) => {
                    this.meetingTable = res.data.data.records;
                    this.pagination.currentPage = res.data.data.current;
                    this.pagination.pageSize = res.data.data.size;
                    this.pagination.total = res.data.data.total;
                })
            },

            // 会议列表分页工具条
            meetingTableSizeChange(val) {
                this.pagination.pageSize = val;
                this.queryMeeting();
            },
            meetingTableCurrentChange(val) {
                this.pagination.currentPage = val;
                this.queryMeeting();
            },

            // 参会人员/签到详情
            meetingDetails(row) {
                this.checkInDialogVisible = true;
                axios.get("/meeting/checkIn/" + row.id).then((res) => {
                    if (res.data.flag) {
                        this.staffTable = res.data.data;
                    }
                })
                axios.get("/meeting/checkInCount/" + row.id).then((res) => {
                    if (res.data.flag) {
                        this.checkInCount = res.data.data;
                    }
                })
            },

            // 表格选中行
            selectOneMeeting(val) {
                this.currentMeetingRow = val;
            },

            // 创建会议 弹窗
            createMeeting() {
                this.createMeetingDialogVisible = true;
                this.createMeetingForm = this.$options.data().createMeetingForm;
            },

            // 创建会议 提交表单
            submitCreateMeetingForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        this.createMeetingForm.sponsor = this.staffInfo.userName;
                        this.createMeetingForm.sponsorId = this.staffInfo.id;
                        this.createMeetingForm.status = '未开始';
                        axios.post("/meeting/create", this.createMeetingForm).then((res) => {
                            if (res.data.flag) {
                                this.$message.success(res.data.msg);
                                this.resetForm('createMeetingForm');
                                this.multipleSelection = this.$options.data().multipleSelection;
                                this.createMeetingDialogVisible = false;
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        }).finally(() => {
                            this.queryMeeting();
                        })
                    } else {
                        this.$message.error('请校验表单后再提交 =_*');
                        return false;
                    }
                });
            },
            // 参会人员 弹窗
            selectStaff() {
                this.selectStaffDialogVisible = true;
                this.queryStaff();
            },
            // 参会人员列表 选择人员
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            // 人员列表选中确认后遍历获取所有id，并赋值给表单等待提交
            confirmSelectStaff() {
                var str = new Array();
                var val = this.multipleSelection;
                for (let i = 0; i < val.length; i++) {
                    str.push(val[i].id);
                }
                this.createMeetingForm.participants = str;
                this.selectStaffDialogVisible = false;
            },

            // 编辑会议 弹窗
            editMeeting() {
                if (this.currentMeetingRow != '') {
                    if (this.currentMeetingRow.status == "未开始") {
                        if (this.currentMeetingRow.sponsorId == this.staffInfo.id) {
                            this.editMeetingDialogVisible = true;
                            this.createMeetingForm = this.currentMeetingRow;
                        } else {
                            this.$message.error('你没有权限修改会议 -_-||');
                        }
                    } else {
                        this.$message.warning('会议只有在 未开始 状态才能修改哦~');
                    }
                } else {
                    this.$message.error('请选择一个会议后再编辑');
                }
            },

            // 编辑会议 提交表单
            submitEditMeetingForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        axios.put("/meeting/edit", this.createMeetingForm).then((res) => {
                            if (res.data.flag) {
                                this.$message.success(res.data.msg);
                                this.createMeetingForm = this.$options.data().createMeetingForm;
                                this.editMeetingDialogVisible = false;
                            } else {
                                this.$message.error(res.data.msg);
                            }
                        }).finally(() => {
                            this.queryMeeting();
                        })
                    } else {
                        this.$message.error('请校验表单后再提交 =_*');
                        return false;
                    }
                });
            },

            // 开始会议 弹窗
            startMeeting() {
                if (this.currentMeetingRow != '') {
                    if (this.currentMeetingRow.sponsorId == this.staffInfo.id) {
                        this.startMeetingDialogVisible = true;
                    } else {
                        this.$message.error('你没有权限开始会议 -_-||');
                    }
                } else {
                    this.$message.error('请选择一个会议后再开始 -_-||');
                }
            },

            // 确认开始会议
            confirmStartMeeting() {
                axios.put("/meeting/start/", this.currentMeetingRow).then((res) => {
                    if (res.data.flag) {
                        this.$message.success(res.data.msg);
                        this.displayCheckInPageDialogVisible = true;
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    this.startMeetingDialogVisible = false;
                    this.queryMeeting();
                })
            },

            // 会议代签 弹窗
            helpCheckIn() {
                if (this.currentMeetingRow != '') {
                    if (this.currentMeetingRow.sponsorId == this.staffInfo.id) {
                        if (this.currentMeetingRow.status == "进行中") {
                            this.helpCheckInDialogVisible = true;
                            axios.get("/meeting/checkIn/" + this.currentMeetingRow.id).then((res) => {
                                this.helpCheckInTableDate = res.data.data;
                            })
                        } else {
                            this.$message.warning('抱歉~只有会议进行时才能代签');
                        }
                    } else {
                        this.$message.error('你没有权限会议代签 -_-||');
                    }
                } else {
                    this.$message.error('请选择一个会议后再操作 -_-||');
                }
            },

            // 会议代签 点击签到
            confirmHelpCheckIn(row) {
                axios.put("/meeting/helpCheckIn/" + row.id + "/" + this.currentMeetingRow.id).then((res) => {
                    if (res.data.flag) {
                        this.$message.success(res.data.msg);
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    axios.get("/meeting/checkIn/" + this.currentMeetingRow.id).then((res) => {
                        this.helpCheckInTableDate = res.data.data;
                    })
                })
            },

            // 删除会议 弹窗
            deleteMeeting() {
                if (this.currentMeetingRow != '') {
                    if (this.currentMeetingRow.sponsorId == this.staffInfo.id) {
                        this.deleteMeetingDialogVisible = true;
                    } else {
                        this.$message.error('你没有权限删除会议 -_-||');
                    }
                } else {
                    this.$message.error('请选择一个会议后再删除 -_-||');
                }
            },

            // 确认删除会议
            confirmDeleteMeeting() {
                axios.delete("/meeting/delete/" + this.currentMeetingRow.id).then((res) => {
                    if (res.data.flag) {
                        this.$message.success(res.data.msg);
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    this.queryMeeting();
                    this.deleteMeetingDialogVisible = false;
                })
            },

            /**
             * 员工管理
             */
            // 员工列表
            queryStaff() {
                let param = "?userName=" + this.staffCondition.userName;
                param += "&dept=" + this.staffCondition.dept;
                param += "&post=" + this.staffCondition.post;
                axios.get("/staff/list/" + this.pagination.currentPage + "/" + this.pagination.pageSize + param).then((res) => {
                    var staffs = res.data.data.records;
                    for (let staff of staffs) {
                        if (staff.admin == 'true') {
                            staff.admin = '是'
                        } else {
                            staff.admin = ''
                        }
                    }
                    this.staffTable = res.data.data.records;
                    this.pagination.currentPage = res.data.data.current;
                    this.pagination.pageSize = res.data.data.size;
                    this.pagination.total = res.data.data.total;
                })
            },

            // 员工列表分页工具条
            staffTableSizeChange(val) {
                this.pagination.pageSize = val;
                this.queryStaff();
            },
            staffTableCurrentChange(val) {
                this.pagination.currentPage = val;
                this.queryStaff();
            },

            // 员工入职 提交表单
            submitStaffHiredForm(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        axios.post("/staff/hired", this.staffHiredForm).then((res) => {
                            if (res.data.flag) {
                                this.$message.success(res.data.msg);
                                this.staffHiredForm = this.$options.data().staffHiredForm;
                                this.fileList = this.$options.data().fileList;
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

            // 重置分页工具条
            restPagination() {
                this.pagination = this.$options.data().pagination;
                this.pagination.currentPage = 1;
                this.pagination.pageSize = 10;
                this.pagination.total = 0;
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
            },

            // 表格选中行
            selectOneStaff(val) {
                this.currentStaffRow = val;
            },

            // 员工信息修改弹窗
            staffEdit() {
                if (this.currentStaffRow != '') {
                    this.staffEditDialogVisible = true;
                    if (this.currentStaffRow.admin == '是') {
                        this.currentStaffRow.admin = true;
                    } else {
                        this.currentStaffRow.admin = false;
                    }
                    this.staffEditForm = this.currentStaffRow;
                }
            },

            // 确认修改信息
            confirmStaffEdit(formName) {
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                        axios.put("/staff/edit", this.staffEditForm).then((res) => {
                            if (res.data.flag) {
                                this.$message.success(res.data.msg);
                                this.staffEditDialogVisible = false;
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

            // 取消修改员工信息
            cancelEditStaff() {
                this.queryStaff();
                this.staffEditDialogVisible = false;
            },

            // 员工离职 弹窗
            staffDimission() {
                if (this.currentStaffRow != '') {
                    this.staffDimissionDialogVisible = true;
                }
            },

            // 确定离职
            dimission() {
                axios.delete("/staff/dimission/" + this.currentStaffRow.id).then((res) => {
                    if (res.data.flag) {
                        this.$message.success(res.data.msg);
                        this.currentStaffRow = '';
                        this.staffDimissionDialogVisible = false;
                    } else {
                        this.$message.error(res.data.msg);
                    }
                }).finally(() => {
                    this.queryStaff();
                })
            },

            /**
             * 公用方法
             */

            // 进去签到页面
            displayCheckInPage() {
                window.open('/checkIn');
                this.displayCheckInPageDialogVisible = false;
            }
        },
        created() {
            // 读取localStorage中的用户信息
            var staffInfo = window.localStorage.getItem("staffInfo");
            if (staffInfo) {
                this.staffInfo = JSON.parse(staffInfo)
            }
            this.queryMyCreatedMeeting();
            this.queryMyParticipantsMeeting();
        }
    })
})