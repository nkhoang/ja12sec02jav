<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
<title><fmt:message key="webapp.title"/></title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.js" type="text/javascript"></script>

<script type="text/javascript" src="<c:url value='/js/ext-all.js' />"></script>
<link href="<c:url value='/resources/css/ext-all-gray.css'/>" rel="stylesheet" media="all"/>
<link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/phonecard-layout.css' />"/>
<style type="text/css">
    .x-body {
        font-family: "Arial", serif !important;
    }

    #captcha {
        text-align: center;
        margin: 0 0 auto;
    }

    #form-container {
        margin: 0 auto;
    }

    #form-container > div {
        margin: 0 auto;
    }

    #content {
    }
</style>

<script type="text/javascript">
Ext.onReady(function () {
    Ext.tip.QuickTipManager.init();

    // The data store holding the states; shared by each of the ComboBox examples below
    var genderStore = Ext.create('Ext.data.Store', {
        fields:['abbr', 'name'],
        data:[
            {"abbr":"Male", "name":"Nam"},
            {"abbr":"Female", "name":"N&#7919;"}
        ]
    });

    var formPanel = Ext.widget('form', {
        renderTo:'form-container',
        frame:true,
        width:450,
        bodyPadding:10,
        bodyBorder:true,
        title:'Th&#244;ng tin &#273;&#259;ng k&#237;',

        defaults:{
            anchor:'100%'
        },
        fieldDefaults:{
            labelAlign:'left',
            msgTarget:'under',
            invalidCls:'' //unset the invalidCls so individual fields do not get styled as invalid
        },

        /*
         * Listen for validity change on the entire form and update the combined error icon
         */
        listeners:{
            fieldvaliditychange:function () {
                // this.updateErrorState();
            },
            fielderrorchange:function () {
                // this.updateErrorState();
            }
        },

        updateErrorState:function () {
            var me = this,
                    errorCmp, fields, errors;

            if (me.hasBeenDirty || me.getForm().isDirty()) { //prevents showing global error when form first loads
                errorCmp = me.down('#formErrorState');
                fields = me.getForm().getFields();
                errors = [];
                fields.each(function (field) {
                    Ext.Array.forEach(field.getErrors(), function (error) {
                        errors.push({name:field.getFieldLabel(), error:error});
                    });
                });
                errorCmp.setErrors(errors);
                me.hasBeenDirty = true;
            }
        },
        items:[
            {
                xtype:'textfield',
                name:'firstName',
                fieldLabel:'<fmt:message key="register.firstName" />',
                allowBlank:false,
                blankText:'<fmt:message key="register.error.missing" />',
                minLength:2,
                minLengthText:'<fmt:message key="register.error.min" />',
                maxLength:30,
                maxLengthText:'<fmt:message key="register.error.max" />'
            },
            {
                xtype:'textfield',
                name:'middleName',
                fieldLabel:'<fmt:message key="register.middleName" />',
                minLength:2,
                minLengthText:'<fmt:message key="register.error.min" />',
                maxLength:30,
                maxLengthText:'<fmt:message key="register.error.max" />'
            },
            {
                xtype:'textfield',
                name:'lastName',
                fieldLabel:'<fmt:message key="register.lastName" />',
                allowBlank:false,
                blankText:'<fmt:message key="register.error.missing" />',
                minLength:2,
                minLengthText:'<fmt:message key="register.error.min" />',
                maxLength:30,
                maxLengthText:'<fmt:message key="register.error.max" />'
            },
            {
                xtype:'textfield',
                name:'username',
                fieldLabel:'<fmt:message key="register.userName" />',
                allowBlank:false,
                blankText:'<fmt:message key="register.error.missing" />',
                minLength:5,
                minLengthText:'<fmt:message key="register.error.min" />',
                maxLength:25,
                maxLengthText:'<fmt:message key="register.error.max" />'
            },
            {
                xtype:'textfield',
                name:'email',
                fieldLabel:'<fmt:message key="register.email" />',
                vtype:'email',
                emailText:'<fmt:message key="register.email.error.invalid" />',
                allowBlank:false,
                blankText:'<fmt:message key="register.error.missing" />'
            },
            {
                xtype:'textfield',
                name:'password1',
                fieldLabel:'<fmt:message key="register.password" />',
                inputType:'password',
                style:'margin-top:15px',
                allowBlank:false,
                blankText:'<fmt:message key="register.error.missing" />',
                minLength:6,
                minLengthText:'<fmt:message key="register.error.min" />',
                maxLength:15,
                maxLengthText:'<fmt:message key="register.error.max" />'
            },
            {
                xtype:'textfield',
                name:'password2',
                fieldLabel:'<fmt:message key="register.repeatPassword" />',
                inputType:'password',
                allowBlank:false,
                blankText:'<fmt:message key="register.error.missing" />',
                /**
                 * Custom validator implementation - checks that the value matches what was entered into
                 * the password1 field.
                 */
                validator:function (value) {
                    var password1 = this.previousSibling('[name=password1]');
                    return (value === password1.getValue()) ? true : '<fmt:message key="register.repeatPassword.error.notMatch" />'
                }
            },
            {
                xtype:'textfield',
                fieldLabel:'<fmt:message key="register.phoneNumber" />',
                name:'phoneNumber',
                hideTrigger:true,
                minLength:7,
                minLengthText:'<fmt:message key="register.error.min" />',
                maxLength:13,
                maxLengthText:'<fmt:message key="register.error.max" />'
            },
            {
                xtype:'datefield',
                name:'birthDate',
                editable:false,
                invalidText:'<fmt:message key="register.error.date.invalid" />',
                fieldLabel:'<fmt:message key="register.birthDate" />'
            },
            {
                xtype:'radiogroup',
                fieldLabel:'<fmt:message key="register.gender" />',
                cls:'x-check-group-alt',
                name:"gender",
                items:[
                    {boxLabel:'<fmt:message key="register.gender.male" />', name:'gender', inputValue:'MALE', checked:true},
                    {boxLabel:'<fmt:message key="register.gender.female" />', name:'gender', inputValue:'FEMALE' }
                ]
            },
            {
                xtype:'textfield',
                fieldLabel:'<fmt:message key="register.personalId" />',
                name:'personalId',
                hideTrigger:true,
                minLength:9,
                minLengthText:'<fmt:message key="register.error.min" />',
                maxLength:20,
                maxLengthText:'<fmt:message key="register.error.max" />'
            },
            {
                xtype:'radiogroup',
                name:'personalIdType',
                fieldLabel:'<fmt:message key="register.personalId.type" />',
                cls:'x-check-group-alt',
                items:[
                    {boxLabel:'CMND', name:'personalIdType', inputValue:'civil', checked:true},
                    {boxLabel:'VISA', name:'personalIdType', inputValue:'visa' }
                ]
            },
            {
                xtype:'textfield',
                name:'issuePlace',
                fieldLabel:'<fmt:message key="register.issuePlace" />'
            },
            {
                xtype:'datefield',
                name:'issueDate',
                editable:false,
                invalidText:'<fmt:message key="register.error.date.invalid" />',
                fieldLabel:'<fmt:message key="register.issueDate" />'
            },
            {
                contentEl:Ext.getDom('captcha'),
                xtype:"panel"
            },
            /*
             * Terms of Use acceptance checkbox. Two things are special about this:
             * 1) The boxLabel contains a HTML link to the Terms of Use page; a special click listener opens this
             *    page in a modal Ext window for convenient viewing, and the Decline and Accept buttons in the window
             *    update the checkbox's state automatically.
             * 2) This checkbox is required, i.e. the form will not be able to be submitted unless the user has
             *    checked the box. Ext does not have this type of validation built in for checkboxes, so we add a
             *    custom getErrors method implementation.
             */
            {
                xtype:'checkboxfield',
                name:'acceptTerms',
                fieldLabel:'<fmt:message key="register.termTitle"/>',
                hideLabel:true,
                style:'margin-top:15px',
                boxLabel:'T&#244;i &#273;&#227; &#273;&#7885;c v&#224; ch&#7845;p nh&#7853;n <a href="http://www.sencha.com/legal/terms-of-use/" class="terms">&#272;i&#7873;u kho&#7843;n s&#7917; d&#7909;ng</a>.',

                // Listener to open the Terms of Use page link in a modal window
                listeners:{
                    click:{
                        element:'boxLabelEl',
                        fn:function (e) {
                            var target = e.getTarget('.terms'),
                                    win;
                            if (target) {
                                win = Ext.widget('window', {
                                    title:'Terms of Use',
                                    modal:true,
                                    html:'<iframe src="' + target.href + '" width="950" height="500" style="border:0"></iframe>',
                                    buttons:[
                                        {
                                            text:'Decline',
                                            handler:function () {
                                                this.up('window').close();
                                                formPanel.down('[name=acceptTerms]').setValue(false);
                                            }
                                        },
                                        {
                                            text:'Accept',
                                            handler:function () {
                                                this.up('window').close();
                                                formPanel.down('[name=acceptTerms]').setValue(true);
                                            }
                                        }
                                    ]
                                });
                                win.show();
                                e.preventDefault();
                            }
                        }
                    }
                },

                // Custom validation logic - requires the checkbox to be checked
                getErrors:function () {
                    return this.getValue() ? [] : ['<fmt:message key="register.acceptTerm" /> ']
                }
            }
        ],

        dockedItems:[
            {
                xtype:'container',
                dock:'bottom',
                layout:{
                    type:'hbox',
                    align:'middle'
                },
                padding:'10 10 5',

                items:[
                    /*
                     {
                     xtype:'component',
                     id:'formErrorState',
                     baseCls:'form-error-state',
                     flex:1,
                     validText:'<fmt:message key="register.form.valid" />',
                     invalidText:'<fmt:message key="register.form.invalid" />',
                     tipTpl:Ext.create('Ext.XTemplate', '<ul><tpl for="."><li><span class="field-name">{name}</span>: <span class="error">{error}</span></li></tpl></ul>'),

                     getTip:function () {
                     var tip = this.tip;
                     if (!tip) {
                     tip = this.tip = Ext.widget('tooltip', {
                     target:this.el,
                     preventHeader:true,
                     title:'<fmt:message key="register.form.errorTitle" />:',
                     autoHide:false,
                     anchor:'right',
                     mouseOffset:[-200, 0],
                     closable:true,
                     constrainPosition:true,
                     cls:'errors-tip'
                     });
                     tip.show();
                     }
                     return tip;
                     },

                     setErrors:function (errors) {
                     var me = this,
                     baseCls = me.baseCls,
                     tip = me.getTip();

                     errors = Ext.Array.from(errors);

                     // Update CSS class and tooltip content
                     if (errors.length) {
                     me.addCls(baseCls + '-invalid');
                     me.removeCls(baseCls + '-valid');
                     me.update(me.invalidText);
                     tip.setDisabled(false);
                     tip.update(me.tipTpl.apply(errors));
                     } else {
                     me.addCls(baseCls + '-valid');
                     me.removeCls(baseCls + '-invalid');
                     me.update(me.validText);
                     tip.setDisabled(true);
                     tip.hide();
                     }
                     }
                     },*/
                    {
                        xtype:'button',
                        formBind:true,
                        disabled:true,
                        text:'<fmt:message key="register.button.submit" />',
                        width:140,
                        handler:function () {
                            var form = this.up('form').getForm();
                            form.submit({
                                clientValidation:true,
                                params:{
                                    recaptchaResponse:$('#recaptcha_response_field').val(),
                                    recaptchaChallenge:$('#recaptcha_challenge_field').val()
                                },
                                url:'<c:url value="/user/registerUser.html" />',
                                success:function (form, action) {
                                    Ext.Msg.alert('Success', action.result.msg + '<fmt:message key="register.success.text" />', function () {
                                        // redirect to the login page.
                                        window.location = "<c:url value="/user/index.html"/>"
                                    });
                                },
                                failure:function (form, action) {
                                    Ext.Msg.alert('Success', action.result.msg);
                                }
                            });
                        }
                    }
                ]
            }
        ]
    });
});
</script>
</head>
<body>
<div id="content">
    <jsp:include page="header.jsp"/>
    <div class="main">
        <div id="form-container" class="m">
        </div>
    </div>
    <jsp:include page="footer.jsp"/>
</div>
<div id="captcha">
    <script type="text/javascript"
            src="http://www.google.com/recaptcha/api/challenge?k=6LdRq8oSAAAAAI5yODeg9MGNN1nxd8dpeBJWX9HF">
    </script>
    <noscript>
        <iframe src="http://www.google.com/recaptcha/api/noscript?k=6LdRq8oSAAAAAI5yODeg9MGNN1nxd8dpeBJWX9HF"
                height="300" width="500" frameborder="0"></iframe>
        <br>
        <textarea name="recaptcha_challenge_field" rows="3" cols="40">
        </textarea>
        <input type="hidden" name="recaptcha_response_field"
               value="manual_challenge">
    </noscript>
</div>
</body>
</html>
