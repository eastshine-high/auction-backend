package com.eastshine.auction.common.security;

import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;

public class SecurityMultipartResolver extends CommonsMultipartResolver {
	// xss, sql injection 체크 하지 않을 파라미터 keyList(comma로 구분)
	// private static String[] excludeFieldsArr = PropertiesUtil.getGlobalProperties().getProperty("filter.notCheckList").split(",");
    // private SqlSession session;
	private boolean resolveLazily = false;

	public SecurityMultipartResolver() {
		super();
	}
	
/*	@Override
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
		
		// Multipart resolver 가 사용하는 directory : FrontStorage 가기 전이에요.
		String tmpDir = PropertiesUtil.getGlobalProperties().getProperty("multipart.temp.dir", "");
		if(tmpDir != null && !"".equals(tmpDir)) {
			if(new java.io.File(tmpDir).exists()) {
				LOGGER.info("File Temporary directory for Multipart is " + tmpDir);
				getFileItemFactory().setRepository(new java.io.File(tmpDir));
			}else {
				LOGGER.warn("globals.properties 의 multipart.temp.dir 값을 확인해 주세요. Directory not found. 기본 Temp Dir 을 사용합니다.");
			}
		}
	}*/

/*    public void setSession(SqlSession session) {
        this.session = session;
    }*/

    public void setResolveLazily(boolean resolveLazily) {
		this.resolveLazily = resolveLazily;
	}
	
	public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
		Assert.notNull(request, "Request must not be null");
		if (this.resolveLazily) {
			return new SecurityMultipartHttpServletRequest(request) {
				@Override
				protected void initializeMultipart() {
					MultipartParsingResult parsingResult = parseRequest(request);
					setMultipartFiles(parsingResult.getMultipartFiles());
					setMultipartParameters(parsingResult.getMultipartParameters());
					setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
				}
			};
		}
		else {
			MultipartParsingResult parsingResult = parseRequest(request);
			return new SecurityMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(),
						parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
		}
	}
}
