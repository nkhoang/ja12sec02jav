package com.nkhoang.gae.action;

import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.*;
import com.nkhoang.gae.model.Dictionary;
import com.nkhoang.gae.service.ApplicationService;
import com.nkhoang.gae.service.TagService;
import com.nkhoang.gae.service.UserService;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import com.nkhoang.search.LuceneSearchFields;
import com.nkhoang.search.LuceneSearchUtils;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/" + ViewConstant.USER_NAMESPACE)
@SessionAttributes("currentUser")
public class UserAction {
    private static Logger LOG = LoggerFactory
            .getLogger(UserAction.class.getCanonicalName());
    public static final String RECENT_WORD_OFFSET_SESSION = "recentWordOffset";
    private static final String REGISTER_SUCCESS_DATA_FIELD = "success";
    private static final String REGISTER_MESSAGE_DATA_FIELD = "msg";
    @Autowired
    private UserService userService;
    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private TagService tagService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private UserManager userManager;

    @Value("#{encryption['recaptcha.private.key']}")
    private String reCaptchaPrivateKey;
    @Value("#{encryption['recaptcha.public.key']}")
    private String reCaptchaPublicKey;

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @RequestMapping("/index")
    public String renderMainPage() {
        return "phonecard/index";
    }

    @RequestMapping("/admin")
    public String renderAdvancedAdminPage() {
        return "user/advancedAdmin";
    }

    @RequestMapping("/register")
    public String renderRegisterPage() {
        return "phonecard/register";
    }

    @RequestMapping("/editUser")
    public String renderEditPage() {
        return "user/edit";
    }

    @RequestMapping("/getUserData")
    public ModelAndView getUserData() {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        User user = userService.getCurrentUser();
        if (user != null) {
            List<String> attrs = new ArrayList<String>();
            attrs.addAll(Arrays.asList(User.SKIP_FIELDS_USER));
            modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);
            jsonData.put("data", user);
            jsonData.put("success", true);
        } else {
            jsonData.put("success", false);
        }
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping("/registerUser")
    public ModelAndView registerUser(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false, defaultValue = "") String middleName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password1,
            @RequestParam(required = false) String password2,
            @RequestParam(required = false) Long phoneNumber,
            @RequestParam(defaultValue = "", required = false) String birthDate,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String personalId,
            @RequestParam(required = false) String personalIdType,
            @RequestParam(required = false) String issueDate,
            @RequestParam(required = false) String issuePlace,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String recaptchaResponse,
            @RequestParam(required = false) String recaptchaChallenge,
            HttpServletRequest request
    ) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();
        boolean isValidRequest = true;
        // firstname, lastname, email are required.
        // password, username are also reqruied but
        if (StringUtils.isNotBlank(firstName)
                && StringUtils.isNotBlank(lastName) && StringUtils.isNotBlank(email)
                ) {
            boolean isSafeToRegister = true;
            if (id == null) {
                // check captcha input
                if (StringUtils.isBlank(recaptchaChallenge) || StringUtils.isBlank(recaptchaResponse)) {
                    jsonData.put(REGISTER_SUCCESS_DATA_FIELD, false);
                    jsonData.put(REGISTER_MESSAGE_DATA_FIELD, "Vui lòng nhập lại mã xác nhận.");
                    isSafeToRegister = false;
                } else {
                    String remoteAddr = request.getRemoteAddr();
                    ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
                    LOG.info("ReCaptcha private key : " + reCaptchaPrivateKey);
                    reCaptcha.setPrivateKey(reCaptchaPrivateKey);

                    ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, recaptchaChallenge, recaptchaResponse);
                    if (!reCaptchaResponse.isValid()) {
                        LOG.debug("Captcha invalid.");
                        isSafeToRegister = false;
                        jsonData.put(REGISTER_SUCCESS_DATA_FIELD, false);
                        jsonData.put(REGISTER_MESSAGE_DATA_FIELD, "Mã xác nhận sai. Vui lòng nhập lại.");
                    }
                }
                // do not want to continue if something went wrong.
                if (isSafeToRegister) {
                    if (StringUtils.isBlank(password1) || StringUtils.isBlank(password2) ||
                            StringUtils.isBlank(username)) {
                        isSafeToRegister = false;
                        jsonData.put(REGISTER_SUCCESS_DATA_FIELD, false);
                        jsonData.put(REGISTER_MESSAGE_DATA_FIELD, "Mật khẩu và Xác nhận mật khẩu không đúng. Vui lòng kiểm tra lại.");
                    } else {
                        // check user by username
                        if (userManager.checkUsername(username)) {
                            isSafeToRegister = false;
                            jsonData.put(REGISTER_SUCCESS_DATA_FIELD, false);
                            jsonData.put(REGISTER_MESSAGE_DATA_FIELD, "Tên đăng nhập đã được đăng kí. Vui lòng dùng chọn tên khác.");
                        }
                        if (userManager.checkEmail(email)) {
                            isSafeToRegister = false;
                            jsonData.put(REGISTER_SUCCESS_DATA_FIELD, false);
                            jsonData.put(REGISTER_MESSAGE_DATA_FIELD, "Email đã được đăng kí. Vui lòng sửa lại email khác.");
                        }
                    }
                }
            }
            if (isSafeToRegister) {
                User user = null;
                if (id == null) {
                    user = new User();
                } else {
                    user = (User) userManager.loadUserById(id);
                }
                if (StringUtils.isNotBlank(password1) && StringUtils.isNotBlank(password2)
                        && StringUtils.equals(password1, password2)) {
                    user.setPassword(userManager.getEncodePassword(password1));
                }
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setMiddleName(middleName);
                if (id == null) {
                    user.setUsername(username);
                }
                user.setPhoneNumber(phoneNumber + "");
                user.setEmail(email);
                // convert birthDate
                if (StringUtils.isNotBlank(birthDate)) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
                        user.setBirthDate(dateFormat.parse(birthDate));
                    } catch (ParseException pex) {
                        LOG.error("Could not parse birth date.");
                    }
                }
                if (StringUtils.isNotBlank(gender)) {
                    user.setGender(gender);
                }
                if (StringUtils.isNotBlank(personalId)) {
                    user.setPersonalId(personalId);
                }
                if (StringUtils.isNotBlank(personalIdType)) {
                    user.setPersonalIdType(personalIdType);
                }
                if (StringUtils.isNotBlank(issueDate)) {
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
                        user.setIssueDate(dateFormat.parse(issueDate));
                    } catch (ParseException pex) {
                        LOG.error("Could not parse issue date.");
                    }
                }
                if (StringUtils.isNotBlank(issuePlace)) {
                    user.setIssuePlace(issuePlace);
                }
                // set role
                user.setRoleNames(Arrays.asList(Role.UserRole.ROLE_USER.name()));
                user.setEnabled(true);
                if (user.getId() != null) {
                    userManager.update(user);
                    LOG.debug("Update user " + user.getUsername() + " successfully.");
                    jsonData.put(REGISTER_SUCCESS_DATA_FIELD, true);
                    jsonData.put(REGISTER_MESSAGE_DATA_FIELD, "Hồ sơ được chỉnh sửa thành công.");
                } else {
                    user = userManager.save(user);
                    if (user != null) {
                        LOG.debug("Save user " + user.getUsername() + " successfully.");
                        jsonData.put(REGISTER_SUCCESS_DATA_FIELD, true);
                        jsonData.put(REGISTER_MESSAGE_DATA_FIELD, "Đăng kí thành công.");
                    }
                }
            }
        } else {
            isValidRequest = false;
        }

        if (!isValidRequest) {
            jsonData.put(REGISTER_SUCCESS_DATA_FIELD, false);
            jsonData.put(REGISTER_MESSAGE_DATA_FIELD, "Bạn nhập thiếu một số thông tin cần thiết. Vui lòng nhập lại.");
        }

        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping(value = "/deleteAppConfig", method = RequestMethod.GET)
    public ModelAndView deleteAppConfig(@RequestParam(required = false) Long id) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();
        if (id == null) {
            jsonData.put("error", "No id provided. Could not delete AppConfig withou id.");
        } else {
            if (applicationService.deleteAppConfig(id)) {

            } else {
                jsonData.put("error", "Could not delete AppConfig with id = " + id);
            }
        }
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping(value = "/saveAppConfig", method = RequestMethod.GET)
    public ModelAndView saveAppConfig(@RequestParam(defaultValue = "") String label,
                                      @RequestParam(defaultValue = "") String value) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();
        if (StringUtils.isEmpty(label) || StringUtils.isEmpty(value)) {
            jsonData.put("error", "Invalid data provided. Please try again.");
        } else {
            AppConfig appConfig = applicationService.saveAppConfig(label, value);
            if (appConfig != null) {
            } else {
                jsonData.put("error", "Could not save AppConfig.");
            }
        }
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }


    @RequestMapping(value = "/getAppConfig", method = RequestMethod.GET)
    public ModelAndView getAllAppConfig() {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        List<AppConfig> appConfigs = applicationService.getApplicationConfiguration();

        jsonData.put("data", appConfigs);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        List<String> attrs = new ArrayList<String>();
        attrs.addAll(Arrays.asList(AppConfig.SKIP_FIELDS));
        modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);

        return modelAndView;
    }

    @RequestMapping(value = "/deleteDict", method = RequestMethod.GET)
    public ModelAndView deleteDict(@RequestParam(required = false) Long dictId) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        Boolean result = userService.deleteDictionary(dictId);

        jsonData.put("data", result);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping(value = "/getAllDicts", method = RequestMethod.GET)
    public ModelAndView getAllDicts() {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        List<Dictionary> dicts = userService.getAllDictionaries();

        jsonData.put("data", dicts);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        List<String> attrs = new ArrayList<String>();
        attrs.addAll(Arrays.asList(Dictionary.SKIP_FIELDS));
        modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);

        return modelAndView;
    }

    @RequestMapping(value = "/addDictionary", method = RequestMethod.POST)
    public ModelAndView addNewDictionary(
            @RequestParam(defaultValue = "") String dictName,
            @RequestParam(defaultValue = "") String dictDescription) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        Map<String, String> responseData = new HashMap<String, String>();
        if (StringUtils.isEmpty(dictName)) {
            responseData.put("error", "Invalid dictionary name.");
        } else {
            Dictionary dict = userService.addNewDictionary(dictName, dictDescription);
            if (dict != null) {
                // just do nothing.
            } else {
                responseData.put("error", "Invalid dictionary name or dictionary is already added.");
            }
        }

        jsonData.put("data", responseData);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

    @RequestMapping("/getTags")
    public ModelAndView getTags(@RequestParam(required = false) Long wordId) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        User user = userService.getCurrentUser();
        Map<Long, String> tagMap = new HashMap<Long, String>();
        if (user != null) {
            if (wordId != null) {
                List<UserTag> userTags = tagService.getTagsByWord(wordId);
                if (CollectionUtils.isNotEmpty(userTags)) {
                    for (UserTag userTag : userTags) {
                        tagMap.put(userTag.getId(), userTag.getTagName());
                    }
                }
            } else {
                List<UserTag> userTags = tagService.getAllUserTags(user.getId());
                if (CollectionUtils.isNotEmpty(userTags)) {
                    for (UserTag userTag : userTags) {
                        tagMap.put(userTag.getId(), userTag.getTagName());
                    }
                }
            }
        }
        jsonData.put("data", tagMap);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView searchVietnamese(@RequestParam("word") String word, HttpServletRequest request) {
        LOG.info("Word = " + word);
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();
        List<Word> words = new ArrayList<Word>();

        if (StringUtils.isNotEmpty(word)) {
            try {
                String path = request.getSession().getServletContext().getRealPath("WEB-INF/classes");
                List<Document> documents = LuceneSearchUtils.performSearchByContent(word, path);
                if (CollectionUtils.isNotEmpty(documents)) {
                    for (Document doc : documents) {
                        String wordDescription = doc.get(LuceneSearchFields.ID);
                        Word w = new Word();
                        w.setDescription(wordDescription);
                        words.add(w);
                    }
                }
            } catch (IOException ioe) {
                LOG.error("Could not open Lucene searcher.", ioe);
            }
        }


        jsonData.put("data", words);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping("/deleteTag")
    public ModelAndView deleteTag(
            @RequestParam(required = false) Long userTagId, @RequestParam(required = false) Long wordId) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        jsonData.put("result", false);
        if (userTagId != null && wordId != null) {
            boolean result = tagService.delete(userTagId, wordId);
            jsonData.put("result", result);
        }

        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping("/saveTag")
    public ModelAndView saveTag(
            @RequestParam(required = false, defaultValue = "") String tagName,
            @RequestParam(required = false) Long wordId) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        jsonData.put("result", false);
        if (StringUtils.isNotBlank(tagName) && wordId != null) {
            try {
                WordTag result = tagService.save(tagName, wordId);
                if (result != null) {
                    jsonData.put("result", true);
                    jsonData.put("data", result.getUserTagId());
                } else {
                    jsonData.put("error", "Tag is existing. Save failed.");
                }
            } catch (Exception e) {
                jsonData.put("error", "Could not save word.");
            }

        } else {
            jsonData.put("error", "Invalid parameter.");
        }
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping("/userPanel")
    public String showUserPanel() {
        return "user/userPanel";
    }

    @RequestMapping("/getWords")
    public ModelAndView getWordsByDate(
            @RequestParam(required = false) String date, @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer size, HttpServletRequest request) {
        if (offset == null) {
            offset = 0;
        }
        if (size != null) {
            // get 1 more to check if there is something need to get in the next turn.
            size = size + 1;
        }
        // the date must be in the format of the client : mm/dd/yy
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();
        List<String> wordList = new ArrayList<String>();
        if (date != null) {
            wordList = userService.getUserIdWordByDate(date, offset, size);
        }
        jsonData.put("data", wordList);
        jsonData.put("offset", offset);
        jsonData.put("nextOffset", offset + wordList.size() - 1);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping("/saveWord")
    public ModelAndView saveWord(@RequestParam(required = false) Long wordId) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        jsonData.put("result", false);
        if (wordId != null) {
            try {
                UserWord userWord = userService.addWord(wordId);
                if (userWord == null) {
                    jsonData.put("error", "Word added before.");
                } else {
                    jsonData.put("result", true);
                }
            } catch (Exception e) {
                jsonData.put("error", "Could not save word.");
            }

        } else {
            jsonData.put("error", "Invalid parameter.");
        }
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping("/authenticate")
    public ModelAndView authenticate(
            @RequestParam(defaultValue = "") String userName, @RequestParam(defaultValue = "") String password) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();
        try {
            Authentication request = new UsernamePasswordAuthenticationToken(userName, password);
            Authentication authenResult = authenticationManager.authenticate(request);

            SecurityContextHolder.getContext().setAuthentication(authenResult);

            User user = (User) authenResult.getPrincipal();

            jsonData.put("result", true);
            jsonData.put("userName", user.getUsername());
        } catch (AuthenticationException aue) {
            jsonData.put("result", false);
        }
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

    @RequestMapping("/" + ViewConstant.LOGIN_REQUEST)
    public ModelAndView login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof User) {
                currentUser = (User) principal;
            }
        }
        Map<String, Object> model = new Hashtable<String, Object>();
        if (currentUser != null) {
            model.put("isAdmin", true);
            model.put("currentUser", currentUser);
        } else {
            model.put("isAdmin", false);
        }
        return new ModelAndView(ViewConstant.LOGIN_VIEW, model);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    public ApplicationService getApplicationService() {
        return applicationService;
    }

    public void setApplicationService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public String getReCaptchaPrivateKey() {
        return reCaptchaPrivateKey;
    }

    public void setReCaptchaPrivateKey(String reCaptchaPrivateKey) {
        this.reCaptchaPrivateKey = reCaptchaPrivateKey;
    }

    public String getReCaptchaPublicKey() {
        return reCaptchaPublicKey;
    }

    public void setReCaptchaPublicKey(String reCaptchaPublicKey) {
        this.reCaptchaPublicKey = reCaptchaPublicKey;
    }
}