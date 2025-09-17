package com.eastshine.auction.common.security;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SecurityMultipartHttpServletRequest extends DefaultMultipartHttpServletRequest {

    private static List<String> extCheckList;
    private static List<String> validMimeTypes;

    // private static final Logger LOGGER = LogManager.getLogger(XSSMultipartResolver.class);


    public SecurityMultipartHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public SecurityMultipartHttpServletRequest(HttpServletRequest request, MultiValueMap<String, MultipartFile> mpFiles,
                                               Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
        super(request);
//			try{
        MultiValueMap<String, MultipartFile> files =
                new org.springframework.util.LinkedMultiValueMap<String, MultipartFile>(java.util.Collections.unmodifiableMap(mpFiles));
        // List<String> extCheckList = getExtCheckList(session);
        // List<String> validMimeTypes = getValidMimeTypes(session);

        if (!files.isEmpty()) {
            java.util.Iterator<Map.Entry<String, List<MultipartFile>>> itr = files.entrySet().iterator();
            while (itr.hasNext()) {
                Map.Entry<String, List<MultipartFile>> entry = itr.next();
                List<MultipartFile> mFiles = entry.getValue();
                for (int i=0; i < mFiles.size(); i++){
                    MultipartFile mFile = mFiles.get(i);
                    String orginFileName = mFile.getOriginalFilename();
                    String extName = "extName"; // StringHelper.replaceNull(FilenameUtils.getExtension(orginFileName), "").toLowerCase();
                    if (!extName.equals("")){
                        if(!extCheckList.contains(extName)){
                            // throw new InvalidStateException(ErrorCode.F0001.getErrorMsg() + " : " + extName, ErrorCode.F0001);
                        }
                        if(!validMimeTypes.contains(mFile.getContentType())){
                            // throw new InvalidStateException(ErrorCode.F0002.getErrorMsg() + " : " + mFile.getContentType(), ErrorCode.F0002);
                        }
                    }
                }
            }
        }
        setMultipartFiles(mpFiles);
        setMultipartParameters(mpParams);
        setMultipartParameterContentTypes(mpParamContentTypes);
    }

    private List<String> getExtCheckList() {
        if (extCheckList != null) {
            return extCheckList;
        }

        extCheckList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
/*        String value = session.selectOne("db.xml.framework.config.selectConfigOne", new HashMap<String, String>() {{
            put("code", "File");
        }});*/
        JsonNode root = null;
        try {
            root = mapper.readTree("value");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        root.get("limitExtension").forEach(node -> extCheckList.add(node.asText()));
        return extCheckList;
    }

    private List<String> getValidMimeTypes() {
        if (validMimeTypes != null) {
            return validMimeTypes;
        }

        // validMimeTypes = session.selectList("db.xml.framework.config.selectSysMime");
        return validMimeTypes;
    }
}
