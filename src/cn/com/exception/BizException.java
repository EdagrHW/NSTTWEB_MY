package cn.com.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

/**
 * “µŒÒ“Ï≥£
 * @ClassName BizException
 * @author zhaoxin
 * @date Jul 4, 2018 4:04:47 PM
 */
public class BizException extends RuntimeException{

	private static final long serialVersionUID = 8247610319171014183L;

	public BizException(Throwable e) {
		super(ExceptionUtil.getMessage(e), e);
	}

	public BizException(String message) {
		super(message);
	}

	public BizException(String message, Object... params) {
		super(StrUtil.format(message, params));
	}

	public BizException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public BizException(Throwable throwable, String message, Object... params) {
		super(StrUtil.format(message, params), throwable);
	}
}
