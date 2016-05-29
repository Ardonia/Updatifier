/*
 * This file is part of Updatifier, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 FlibioStudio <http://github.com/FlibioStudio>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.flibio.updatifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpUtils {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static String requestData(String url) {
        URL obj;
        try {
            obj = new URL(url);
        } catch (MalformedURLException e) {
            return "";
        }
        HttpURLConnection con;
        try {
            con = (HttpURLConnection) obj.openConnection();
        } catch (IOException e) {
            return "";
        }

        // optional default is GET
        try {
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            return "";
        }

        // add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            return "";
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        try {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException e) {
            return "";
        }
        try {
            in.close();
        } catch (IOException e) {
            return "";
        }
        con.disconnect();
        // return result
        return response.toString();
    }

}
