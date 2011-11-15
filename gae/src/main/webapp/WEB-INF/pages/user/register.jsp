<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.js" type="text/javascript"></script>

    <script type="text/javascript" src="<c:url value='/js/ext-all.js' />"></script>
    <link href="<c:url value='/resources/css/ext-all-gray.css'/>" rel="stylesheet" media="all"/>
   <script type="text/javascript">
         Ext.onReady(function() {
    Ext.tip.QuickTipManager.init();

    // The data store holding the states; shared by each of the ComboBox examples below
     var genderStore = Ext.create('Ext.data.Store', {
          fields: ['abbr', 'name'],
          data : [
              {"abbr":"Male", "name":"Nam"},
              {"abbr":"Female", "name":"N&#7919;"}
          ]
    });

    var formPanel = Ext.widget('form', {
        renderTo: Ext.getBody(),
        frame: true,
        width: 650,
        bodyPadding: 10,
        bodyBorder: true,
        title: 'Th&#244;ng tin &#273;&#259;ng k&#237;',

        defaults: {
            anchor: '100%'
        },
        fieldDefaults: {
            labelAlign: 'left',
            msgTarget: 'none',
            invalidCls: '' //unset the invalidCls so individual fields do not get styled as invalid
        },

        /*
         * Listen for validity change on the entire form and update the combined error icon
         */
        listeners: {
            fieldvaliditychange: function() {
                this.updateErrorState();
            },
            fielderrorchange: function() {
                this.updateErrorState();
            }
        },

        updateErrorState: function() {
            var me = this,
                errorCmp, fields, errors;

            if (me.hasBeenDirty || me.getForm().isDirty()) { //prevents showing global error when form first loads
                errorCmp = me.down('#formErrorState');
                fields = me.getForm().getFields();
                errors = [];
                fields.each(function(field) {
                    Ext.Array.forEach(field.getErrors(), function(error) {
                        errors.push({name: field.getFieldLabel(), error: error});
                    });
                });
                errorCmp.setErrors(errors);
                me.hasBeenDirty = true;
            }
        },

        items: [{
            xtype: 'textfield',
            name: 'firstName',
            fieldLabel: 'T&#234;n',
            allowBlank: false,
            minLength: 30
        },
        {
            xtype: 'textfield',
            name: 'middleName',
            fieldLabel: 'T&#234;n &#272;&#7879;m',
            allowBlank: false,
            minLength: 30
        },
        {
            xtype: 'textfield',
            name: 'lastName',
            fieldLabel: 'H&#7885;',
            allowBlank: false,
            minLength: 30
        },
            {
            xtype: 'textfield',
            name: 'username',
            fieldLabel: 'T&#234;n &#273;&#259;ng nh&#7853;p',
            allowBlank: false,
            minLength: 6
        }, {
            xtype: 'textfield',
            name: 'email',
            fieldLabel: '&#272;&#7883;a ch&#7881; Email',
            vtype: 'email',
            allowBlank: false
        }, {
            xtype: 'textfield',
            name: 'password1',
            fieldLabel: 'M&#7853;t kh&#7849;u',
            inputType: 'password',
            style: 'margin-top:15px',
            allowBlank: false,
            minLength: 8
        }, {
            xtype: 'textfield',
            name: 'password2',
            fieldLabel: 'Nh&#7853;p l&#7841;i m&#7853;t kh&#7849;u',
            inputType: 'password',
            allowBlank: false,
            /**
             * Custom validator implementation - checks that the value matches what was entered into
             * the password1 field.
             */
            validator: function(value) {
                var password1 = this.previousSibling('[name=password1]');
                return (value === password1.getValue()) ? true : 'Passwords do not match.'
            }
        },
        {
            xtype: 'numberfield',
            fieldLabel: 'S&#7889; &#273;i&#7879;n tho&#7841;i',
            name: 'phoneNumber',
            hideTrigger: true,
            minValue: 8,
            maxValue: 11
        },
        {
            xtype: 'datefield',
            name: 'birthDate',
            fieldLabel: 'Ng&#224;y sinh'
        }, {
            xtype: 'radiogroup',
            fieldLabel: 'Gi&#7899;i t&#237;nh',
            cls: 'x-check-group-alt',
            items: [
                {boxLabel: 'Nam', name: 'rb-auto', inputValue: 'male', checked: true},
                {boxLabel: 'N&#7919;', name: 'rb-auto', inputValue: 'female' }
            ]
        },
        {
            xtype: 'numberfield',
            fieldLabel: 'S&#7889; ch&#7913;ng minh',
            name: 'personalId',
            hideTrigger: true,
            minValue: 11,
            maxValue: 20
        },
        {
            xtype: 'radiogroup',
            name: 'personalIdType',
            fieldLabel: 'Lo&#7841;i',
            cls: 'x-check-group-alt',
            items: [
                {boxLabel: 'CMND', name: 'rb-auto', inputValue: 'civil', checked: true},
                {boxLabel: 'VISA', name: 'rb-auto', inputValue: 'visa' }
            ]
        }, {
            xtype: 'textfield',
            name: 'issuePlace',
            fieldLabel: 'N&#417;i C&#7845;p',
            allowBlank: false
        },
        {
            xtype: 'datefield',
            name: 'issueDate',
            fieldLabel: 'Ng&#224;y c&#7845;p'
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
            xtype: 'checkboxfield',
            name: 'acceptTerms',
            fieldLabel: 'Terms of Use',
            hideLabel: true,
            style: 'margin-top:15px',
            boxLabel: 'T&#244;i &#273;&#227; &#273;&#7885;c v&#224; ch&#7845;p nh&#7853;n <a href="http://www.sencha.com/legal/terms-of-use/" class="terms">&#272;i&#7873;u kho&#7843;n s&#7917; d&#7909;ng</a>.',

            // Listener to open the Terms of Use page link in a modal window
            listeners: {
                click: {
                    element: 'boxLabelEl',
                    fn: function(e) {
                        var target = e.getTarget('.terms'),
                            win;
                        if (target) {
                            win = Ext.widget('window', {
                                title: 'Terms of Use',
                                modal: true,
                                html: '<iframe src="' + target.href + '" width="950" height="500" style="border:0"></iframe>',
                                buttons: [{
                                    text: 'Decline',
                                    handler: function() {
                                        this.up('window').close();
                                        formPanel.down('[name=acceptTerms]').setValue(false);
                                    }
                                }, {
                                    text: 'Accept',
                                    handler: function() {
                                        this.up('window').close();
                                        formPanel.down('[name=acceptTerms]').setValue(true);
                                    }
                                }]
                            });
                            win.show();
                            e.preventDefault();
                        }
                    }
                }
            },

            // Custom validation logic - requires the checkbox to be checked
            getErrors: function() {
                return this.getValue() ? [] : ['B&#7841;n ph&#7843;i ch&#7853;p nh&#7853;n &#272;i&#7873;u kho&#7843;n s&#7917; d&#7909;ng tr&#432;&#7899;c khi ti&#7871;p t&#7909;c']
            }
        }],

        dockedItems: [{
            xtype: 'container',
            dock: 'bottom',
            layout: {
                type: 'hbox',
                align: 'middle'
            },
            padding: '10 10 5',

            items: [{
                xtype: 'component',
                id: 'formErrorState',
                baseCls: 'form-error-state',
                flex: 1,
                validText: 'Form is valid',
                invalidText: 'Form has errors',
                tipTpl: Ext.create('Ext.XTemplate', '<ul><tpl for="."><li><span class="field-name">{name}</span>: <span class="error">{error}</span></li></tpl></ul>'),

                getTip: function() {
                    var tip = this.tip;
                    if (!tip) {
                        tip = this.tip = Ext.widget('tooltip', {
                            target: this.el,
                            title: 'Error Details:',
                            autoHide: false,
                            anchor: 'top',
                            mouseOffset: [-11, -2],
                            closable: true,
                            constrainPosition: false,
                            cls: 'errors-tip'
                        });
                        tip.show();
                    }
                    return tip;
                },

                setErrors: function(errors) {
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
            }, {
                xtype: 'button',
                formBind: true,
                disabled: true,
                text: 'Submit Registration',
                width: 140,
                handler: function() {
                    var form = this.up('form').getForm();

                    /* Normally we would submit the form to the server here and handle the response...
                    form.submit({
                        clientValidation: true,
                        url: 'register.php',
                        success: function(form, action) {
                           //...
                        },
                        failure: function(form, action) {
                            //...
                        }
                    });
                    */

                    if (form.isValid()) {
                        Ext.Msg.alert('Submitted Values', form.getValues(true));
                    }
                }
            }]
        }]
    });
});
   </script>
</head>
<body>

</body>
</html>
