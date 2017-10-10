cmake_minimum_required(VERSION 3.4.1)

#添加bzip2目录，为构建添加一个子路径

add_library(
             native-lib

             SHARED

             src/main/cpp/bspatch.c
             )


find_library(
              log-lib

              log )


target_link_libraries(
                       native-lib

                       ${log-lib} )