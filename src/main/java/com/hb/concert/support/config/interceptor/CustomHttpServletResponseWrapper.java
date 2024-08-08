package com.hb.concert.support.config.interceptor;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class CustomHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
    private ServletOutputStream servletOutputStream = new CustomServletOutputStream(byteArrayOutputStream);
    private int httpStatus;

    public CustomHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override public PrintWriter getWriter() throws IOException {
        return printWriter;
    }

    @Override public void setStatus(int sc) {
        super.setStatus(sc);
        this.httpStatus = sc;
    }

    @Override public int getStatus() {
        return httpStatus;
    }

    public byte[] getContentAsByteArray() {
        return byteArrayOutputStream.toByteArray();
    }

    private class CustomServletOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream byteArrayOutputStream;

        public CustomServletOutputStream(ByteArrayOutputStream byteArrayOutputStream) {
            this.byteArrayOutputStream = byteArrayOutputStream;
        }

        @Override public void write(int b) throws IOException {
            byteArrayOutputStream.write(b);
        }

        @Override public boolean isReady() {
            return true;
        }

        @Override public void setWriteListener(WriteListener writeListener) {
            // no-op
        }
    }
}