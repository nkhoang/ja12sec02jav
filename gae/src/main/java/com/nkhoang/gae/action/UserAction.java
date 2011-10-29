package com.nkhoang.gae.action;

import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.model.Dictionary;
import com.nkhoang.gae.model.*;
import com.nkhoang.gae.service.TagService;
import com.nkhoang.gae.service.UserService;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import com.nkhoang.search.LuceneSearchFields;
import com.nkhoang.search.LuceneUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.*;

@Controller
@RequestMapping("/" + ViewConstant.USER_NAMESPACE)
@SessionAttributes("currentUser")
public class UserAction {
    private static Logger LOG = LoggerFactory
            .getLogger(UserAction.class.getCanonicalName());
    public static final String RECENT_WORD_OFFSET_SESSION = "recentWordOffset";
    @Autowired
    private UserService userService;
    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private TagService tagService;

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @RequestMapping("/admin")
    public String renderAdminPage() {
        return "user/admin";
    }


    @RequestMapping(value="/getAllDicts", method = RequestMethod.POST)
    public ModelAndView getAllDicts() {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        List<Dictionary> dicts = userService.getAllDictionaries();

        jsonData.put("data", dicts);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        List<String> attrs = new ArrayList<String>();
        attrs.addAll(Arrays.asList(Word.SKIP_FIELDS));
        modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);

        return modelAndView;
    }

    @RequestMapping( value = "/addDictionary", method = RequestMethod.POST)
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
                List<Document> documents = LuceneUtils.performSearchByContent(word, path);
                if (CollectionUtils.isNotEmpty(documents)) {
                    List<String> foundWords = new ArrayList<String>();
                    for (Document doc : documents) {
                        foundWords.add(doc.get(LuceneSearchFields.WORD_DESCRIPTION));
                    }
                    for (String w : foundWords) {
                        Word fullWord = vocabularyService.lookupWord(w);
                        if (fullWord != null) {
                            words.add(fullWord);
                        } else {
                            Word newWord = new Word();
                            newWord.setDescription(w);
                            words.add(newWord);
                        }
                    }
                }
            } catch (IOException ioe) {
                LOG.error("Could not open Lucene searcher.", ioe);
            }
        }

        List<String> attrs = new ArrayList<String>();
        attrs.addAll(Arrays.asList(Word.SKIP_FIELDS));
        modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);

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

}