<#if params.status.installed>
    <#assign integrationId=22>

    <style>
        .wrapper {
            display: table-cell;
            vertical-align: middle;
        }

        .inner {
            display: inline-block;
            vertical-align: middle;
            font-size: 14px;
        }

        .tgSwitch {
            height: 15px;
            width: 40px;
            background: #d8d9db;
            padding: 3px;
            border-radius: 20px;
            transition: .4s;
            cursor: pointer;
            vertical-align: middle;
        }

        .tgSwitch.active {
            background: #93c0bd;
        }

        .tgSwitch > .slider {
            background: #57938a;
            border-radius: 50%;
            width: 15px;
            height: 15px;
            transition: .4s;
        }

        .tgSwitch > input {
            display: none;
        }

        .disabledbutton {
            pointer-events: none;
            opacity: 0.4;
        }
    </style>

    <form id="settingsForm"
          action="${params.settings.baseUrl}/secure/install/settings/orgId/${orgCustomerId}"
          method="post">

        <hr/>

        <@cms name="${integrationId}/settings/title">
        <h3>Integration Settings</h3>
        </@>

        <div class="block">

            <div class="left">

                <#if (params.timezoneList??)>
                    <@cms name="${integrationId}/settings/time_zone/header">
                    <h4><b>Time Zone</b></h4>
                    </@>

                    <@cms name="${integrationId}/settings/time_zone/description">
                    <p>Please select your company location's time zone. This setting affects the timestamp on your
                        messages.</p>
                    </@>
                <div class="block" style="font-size: 14px">
                    <div class="left" style="margin-right: 30px">
                        <div class="select_style" style="width: 200px; margin-top: 5px">
                            <select style="position: relative;" disabled="disabled" id="timezoneId"
                                    name="settings.timezone">
                            <#list params.timezoneList as timezone>
                                <option id="${timezone.id}" value="${timezone.id}">${timezone.description}</option>
                            </#list>
                            </select><span></span>
                        </div>
                    </div>
                </div>
                </#if>

                <@cms name="${integrationId}/settings/contactSave/header">
                <h4><b>Contact save</b></h4>
                </@>

                <@cms name="${integrationId}/settings/contactSave/description">
                    <p>Automatically retrieve your contact's details from Claim Center and store them inside Zipwhip.</p>
                </@>

                <div class="wrapper">
                    <label for="contactToggle">Disabled</label>
                    <div class="inner">
                        <div id="contactSaveSwitch" class="tgSwitch disabledbutton">
                            <div class="slider"></div>
                            <input type="checkbox" id="contactToggle" name="settings.contactSyncFromCrmEnabled" checked="false"
                                   <#if (params.settings.contactSyncFromCrmEnabled?has_content) >
                                        value="${(params.settings.contactSyncFromCrmEnabled)?string('true', 'false')}"
                                   <#else>
                                        value="true"
                                   </#if>
                            >
                        </div>
                    </div>
                    <label for="conversationToggle">Enabled</label>
                </div>

                <@cms name="${integrationId}/settings/contactSave/footer">
                <p>If disabled, Customer's details such as First Name, Last Name, Address will not be stored within
                    Zipwhip.</p>
                </@>

                <@cms name="${integrationId}/settings/conversation_archive/header">
                <h4><b>Conversation Archive</b></h4>
                </@>

                <@cms name="${integrationId}/settings/conversation_archive/description">
                    <p>After the selected time period passes with no texting activity, all the messages will be archived
                        in a single note on the claim within Claim Center. For e.g., if you choose 4 hours as the time
                        period, once no new messages are exchanged between the adjustor and the customer for 4 hours,
                        all the messages will be saved as a single note within Claim Center.</p>
                </@>

                <@cms name="${integrationId}/settings/conversation_archive/intervalHeader">
                    <p>Please choose a time interval (default is 4 hours):</p>
                </@>

                <div class="block" style="font-size: 14px">
                    <div class="left" style="margin-right: 30px">
                        <div class="select_style" style="width: 200px; margin-top: 5px">
                            <select style="position: relative;" disabled="disabled" id="conversationWindow"
                                    name="settings.conversationWindow">
                                <option id="4" value="4">4 Hours</option>
                                <option id="0" value="0">Realtime</option>
                            </select><span></span>
                        </div>
                    </div>
                </div>

                <p></p>

                <div class="wrapper">
                    <label for="conversationToggle">Disabled</label>
                    <div class="inner">
                        <div id="conversationSwitch" class="tgSwitch disabledbutton">
                            <div class="slider"></div>
                            <input type="checkbox" id="conversationToggle" name="settings.messageArchiveToCrmEnabled" checked="false"
                                   <#if params.settings.messageArchiveToCrmEnabled?has_content >
                                        value="${(params.settings.messageArchiveToCrmEnabled)?string('true', 'false')}"
                                   <#else>
                                        value="true"
                                   </#if>
                            >
                        </div>
                    </div>
                    <label for="conversationToggle">Enabled</label>
                </div>

                <@cms name="${integrationId}/settings/conversation_archive/footer">
                    <p>If disabled, text messages between Zipwhip users and customers will not be saved within Claim
                        Center.</p>
                </@>

                <@cms name="${integrationId}/settings/auto_text_notifications/header">
                <h4><b>Auto Text Notifications</b></h4>
                </@>

                <@cms name="${integrationId}/settings/auto_text_notifications/description">
                    <p>Allows customers to be automatically notified via texts of activities paramsured within Claim
                        Center.</p>
                </@>

                <div class="wrapper">
                    <label for="textToggle">Disabled</label>
                    <div class="inner">
                        <div id="textSwitch" class="tgSwitch disabledbutton">
                            <div class="slider"></div>
                            <input type="checkbox" id="textToggle" name="settings.textFromCrmEnabled" checked="false"
                                   <#if (params.settings.textFromCrmEnabled?has_content) >
                                        value="${(params.settings.textFromCrmEnabled)?string('true', 'false')}"
                                   <#else>
                                        value="true"
                                   </#if>
                            >
                        </div>
                    </div>
                    <label for="conversationToggle">Enabled</label>
                </div>

                <@cms name="${integrationId}/settings/auto_text_notifications/instructions">
                    Please see the ‘Text from CRM’ documentation for further instructions on where to use these values.
                </@>

                <@cms name="${integrationId}/settings/auto_text_notifications/footer">
                    <p>If disabled, customers will not receive auto text notifications.</p>
                </@>

                <@cms name="${integrationId}/settings/auto_text_notifications/url">
                    <p>URL to send automatic texts to - ${params.settings.textFromCrmUrl}</p>
                </@>

                <@cms name="${integrationId}/settings/auto_text_notifications/api_key">
                    <p>API Key - ${params.settings.orgToken}</p>
                </@>

                <@cms name="${integrationId}/settings/auto_text_notifications/org_customer_id">
                    <p>Org Customer Id - ${params.settings.orgCustomerId}</p>
                </@>

            </div>

            <button data-testid="EditSettingsButton" class="cancel right editSettings" type="button"
                    onclick="toggleSettings()">Edit Settings
            </button>

        </div>

        <div class="block settingsEdit" style="display: none">
            <button data-testid="SaveButton" class="right" type="submit" style="margin-left: 10px;">Save</button>
            <a href="${params.settings.baseUrl}/secure/install/settings/orgId/${orgCustomerId}">
                <button data-testid="CancelButton" class="cancel right" type="button">Cancel</button>
            </a>
        </div>

    </form>

    <script type="text/javascript">

        var isEditMode = false;

        function toggleSettings(button) {
            if (!isEditMode) {
                $('.settingsView').hide();
                $('.settingsEdit').show();
                $('.editSettings').hide();
                $('#settingsForm input, #settingsForm select').removeAttr('disabled');
                $('#contactSaveSwitch, #conversationSwitch, #textSwitch').removeClass('disabledbutton');

            } else {
                $('.settingsView').show();
                $('.settingsEdit').hide();
                $('.editSettings').show();
                $('#settingsForm input, #settingsForm select').attr('disabled', 'disabled');
                $('#contactSaveSwitch, #conversationSwitch, #textSwitch').addClass('disabledbutton');
            }
            isEditMode = !isEditMode;
        }

        <#if (params?? && params.settings.timezone??)>
            $(document.getElementById('${params.settings.timezone}')).attr('selected', 'selected');
        </#if>

        <#if (params?? && params.settings.conversationWindow??)>
            $(document.getElementById('${params.settings.conversationWindow}')).attr('selected', 'selected');
        </#if>

        $('document').ready(function () {
            $('.tgSwitch').each(function () {
                if ($(this).find('input').attr('value') == 'true') {
                    $(this).addClass('active');
                    $(this).find('.slider').css({"-webkit-transform": "translateX(25px)"});
                }
            });
        });


        $('.tgSwitch').click(function () {
            if ($(this).find('input').attr('value') == 'true') {
                $(this).removeClass('active');
                $(this).find('input').attr('value', false);
                $(this).find('.slider').css({"-webkit-transform": "translateX(0px)"});
            } else {
                $(this).addClass('active');
                $(this).find('input').attr('value', true);
                $(this).find('.slider').css({"-webkit-transform": "translateX(25px)"});
            }
        });

        <#if (params?? && params.settings.contactSyncFromCrmEnabled?? && !params.settings.contactSyncFromCrmEnabled)>
            var contactSaveSwitch = $('#contactSaveSwitch');
            contactSaveSwitch.removeClass('active');
            contactSaveSwitch.find('input').attr('value', false);
            contactSaveSwitch.find('.slider').css({"-webkit-transform": "translateX(0px)"});
        </#if>

        <#if (params?? && params.settings.messageArchiveToCrmEnabled?? && !params.settings.messageArchiveToCrmEnabled)>
            var conversationArchiveSwitch = $('#conversationSwitch');
            conversationArchiveSwitch.removeClass('active');
            conversationArchiveSwitch.find('input').attr('value', false);
            conversationArchiveSwitch.find('.slider').css({"-webkit-transform": "translateX(0px)"});
        </#if>

        <#if (params?? && params.settings.textFromCrmEnabled?? && !params.settings.textFromCrmEnabled)>
            var autoTextNotificationsSwitch = $('#textSwitch');
            autoTextNotificationsSwitch.removeClass('active');
            autoTextNotificationsSwitch.find('input').attr('value', false);
            autoTextNotificationsSwitch.find('.slider').css({"-webkit-transform": "translateX(0px)"});
        </#if>

    </script>

</#if>
