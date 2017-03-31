/*
 * Copyright (C) 2016 Get Remark
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wj.rotate.com.rotate;

/**
 * Created by jiangwei on 3/31/17.
 */
public interface ViewEditListener {
    int TYPE_CAPTION = 0X01;
    int TYPE_IMAGE = 0X02;

    void onViewEdit(int type);
}
