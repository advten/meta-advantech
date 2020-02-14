/*
 * Copyright (C) 2010 Freescale Semiconductor, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <stdarg.h>
#include <errno.h>
#include <fcntl.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/fb.h>
#include <linux/input.h>
#include <sys/mman.h>
#include <getopt.h>

#define VER_MAIN 0
#define VER_MIN 9

const unsigned int char_32x32[][32] = {
	{ // black;
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
	},
	{ // white
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
		0xffffffff,
	},
	{ // H
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000fffff,
		0x000fffff,
		0x000fffff,
		0x000fffff,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x000f000f,
		0x00000000,
		0x00000000,
		0x00000000,
		0x00000000,
	},
};

const unsigned char char_8x8[][8] = {
	{ // black
		0x00,
		0x00,
		0x00,
		0x00,
		0x00,
		0x00,
		0x00,
		0x00
	},
	{ // white
		0xff,
		0xff,
		0xff,
		0xff,
		0xff,
		0xff,
		0xff,
		0xff
	},
	{	// H
		0x11,
		0x11,
		0x11,
		0x1F,
		0x11,
		0x11,
		0x11,
		0x00
	},
};

typedef enum
{
	DRAW_PATTERN_TYPE_BLACK = 0,
	DRAW_PATTERN_TYPE_WHITE,
	DRAW_PATTERN_TYPE_H,
} DRAW_PATTERN_TYPE;

static const char fb_dev[] = "/dev/fb0";
static struct fb_var_screeninfo info;
static struct fb_fix_screeninfo finfo;
static void *scrbuf;
static int stop_drawing = 0;

static int font_size = 32;
static int delay_time_ms = 50;

const char *short_options = "f:t:h";
const struct option long_options[] = {
	{"font-size", required_argument, NULL, 'f'},
	{"delay-time", required_argument, NULL, 't'},
	{"help", no_argument, NULL, 'h'},
	{0, 0, 0, 0},
};

static void display_help(char *exec_filename)
{
	printf("%s version %d.%d\n", exec_filename, VER_MAIN, VER_MIN);
	printf("Usage: %s [-f] [-t]\n", exec_filename);
	printf("\t-f, font_size\t\t\tfont size 8 or 32, default = %d\n", font_size);
	printf("\t-t, delay-time\t\t\tdelay time in ms, default = %d ms\n", delay_time_ms);
	printf("\t-h, help\t\t\thelp message\n");
}

static void draw_rectangle(int x, int y, DRAW_PATTERN_TYPE pattern_type)
{
	int px_byte = info.bits_per_pixel / 8;
	int h_start, v_start;
	int phy_x_res;
	int location_start;
	int i, j;
	__u32 *buf32;
	__u32 pixel = 0;
	
	//printf("x = %d, y = %d\n", x, y);
	
	phy_x_res = finfo.line_length/px_byte;

	h_start = (x + y * phy_x_res) * px_byte;
	v_start = (x + y * phy_x_res) * px_byte;
	//printf("h_start = %d, v_start = %d\n", h_start, v_start);
	
	location_start = phy_x_res * info.yoffset * px_byte;
	//printf("location_start = %d\n", location_start);
		
	switch (info.bits_per_pixel) {
		case 16:
		case 24:
			break;
			
		case 32:
			for(i=0; i<font_size; i++) {
				int disp_y_offset = i * phy_x_res * px_byte;
				
				buf32 = (__u32*)((__u8*)scrbuf + location_start + h_start + disp_y_offset);
				for(j=0; j<font_size; j++) {
					unsigned int font_data = (font_size == 8)? 
																		(char_8x8[pattern_type][i]): 
																		(char_32x32[pattern_type][i]);

					pixel = (((font_data & (1 << ((font_size - 1) - j))) == 0)? 0x000000: 0xffffff00);
					//printf("char_8x8[%d][%d] = 0x%04X, pixel = 0x%08X\n", pattern_type, i, char_8x8[pattern_type][i], pixel);
					*buf32 = pixel;
					buf32++;
				}
			}
			break;
			
		default:
			break;		
	}
}

static void draw_patterns(void)
{
	int px_byte = info.bits_per_pixel/8;
	int x_res = (finfo.line_length)/px_byte;
	int y_res = info.yres;
	int x_rect_count = x_res/font_size;
	int y_rect_count = y_res/font_size;
	int i, j;
	
	//printf("x_res = %d, y_res = %d\n", x_res, y_res);
	//printf("x_rect_count = %d, y_rect_count = %d\n", x_rect_count, y_rect_count);
	
	for(i=0; i<y_rect_count; i++) {
		for(j=0; j<x_rect_count; j++) {
			if(stop_drawing == 0) {
				draw_rectangle(j * font_size, i * font_size, DRAW_PATTERN_TYPE_BLACK);
				usleep(delay_time_ms * 1000);
				draw_rectangle(j * font_size, i * font_size, DRAW_PATTERN_TYPE_WHITE);
				usleep(delay_time_ms * 1000);
				draw_rectangle(j * font_size, i * font_size, DRAW_PATTERN_TYPE_H);
			}
		}
	}	
}

int main(int argc, char **argv)
{
	int fb_fd;
	int option;
	
	while((option = getopt_long(argc, argv, short_options, long_options, NULL)) != -1) {
		switch(option) {
		case 'f': 
			font_size = atoi(optarg);
			if(font_size != 8 && font_size != 32)
			{
				printf("--> invalid font_size, only 8 and 32 are supported\n");
				goto end_label;
			}
			break;
		case 't': delay_time_ms = atoi(optarg);	break;
		case 'h':	display_help(argv[0]);				goto end_label;
		}
	}

	// stop x-server service
	system("/etc/init.d/xserver-nodm stop");
	
	sleep(5);

	/* read framebuffer for resolution */
	fb_fd = open(fb_dev, O_RDWR);
	if (fb_fd <= 0) {
		goto err_fb;
	}
	if (-1 == ioctl(fb_fd, FBIOGET_VSCREENINFO, &info)) {
		goto err_fb;
	}

	/* map buffer */
	if (ioctl(fb_fd, FBIOGET_FSCREENINFO, &finfo) == -1) {
		goto err_fb;
	}

	printf("Screen resolution: %dx%d, info.xoffset = %d, info.yoffset =%d\n", info.xres, info.yres,info.xoffset,info.yoffset);	
	printf("finfo.smem_len = %d\n", finfo.smem_len);
	printf("finfo.line_length = %d\n", finfo.line_length);
	
	scrbuf = (__u16*) mmap(0, finfo.smem_len,
			    PROT_READ | PROT_WRITE,
			    MAP_SHARED,
			    fb_fd, 0);
	if (scrbuf== MAP_FAILED) {
		goto err_fb;
	}
	
	memset(scrbuf, 0x00, finfo.smem_len);
	
	while(1)
		draw_patterns();

err_map:
	munmap(scrbuf, finfo.smem_len);
	
err_fb:
	close(fb_fd);

end_label:
    return 0;
}
