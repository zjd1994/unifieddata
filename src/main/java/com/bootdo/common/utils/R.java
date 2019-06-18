package com.bootdo.common.utils;

import java.util.HashMap;
import java.util.Map;

import com.bootdo.common.config.Constant;

public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public R() {
		put("code", 200);
		put("msg", "操作成功");
	}

	public static R error() {
		return error(Constant.STATUS_SERVERERROR, "操作失败");
	}

	public static R error(String msg) {
		return error(Constant.STATUS_SERVERERROR, msg);
	}

	public static R error(String code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok() {
		return new R();
	}

	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
