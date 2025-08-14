package com.michelmaia.screenmach.service;

public interface IDataConverter {
   <T> T getData(String json, Class<T> clazz);
}
