#include <jni.h>
#include <iostream>
#include <list>
#include <string.h>
#include <stdlib.h>
#include <math.h>
using namespace std;

#define dRate 		1.5				//원의 지름
#define MaxDistance	10.0			//10Km
#define NomalRate	10				//10배의 환경
#define Speed		0.25			//250m/s의 속도
#define _time(dist)	(dist)*NomalRate/Speed

typedef struct {
	unsigned int R_ID;
	unsigned int S_ID;
	char UD;
	unsigned int Index;
	char B_NM[11];
	char S_NM[56];
	int Time;
	double x;
	double y;
} INFO;

typedef struct {
	unsigned int R_ID;
	int maxDelay;
} LINE;

typedef struct {
	list<INFO *>::iterator it;
	int Time;
	list<list<INFO *>::iterator> Path;
} STATE;

#define PI 3.14159265358979323846264338327950288419
static const double R = 6372.797560856;
static const double DEG_TO_RAD = PI / 180.0;
static const double RAD_TO_DEG = 180.0 / PI;
double Haversine_Distance(double lat1, double lon1, double lat2, double lon2) {
	double dlon = (lon2 - lon1) * DEG_TO_RAD;
	double dlat = (lat2 - lat1) * DEG_TO_RAD;
	double a = pow(sin(dlat * 0.5), 2)
			+ cos(lat1 * DEG_TO_RAD) * cos(lat2 * DEG_TO_RAD)
					* pow(sin(dlon * 0.5), 2);
	double c = 2.0 * atan2(sqrt(a), sqrt(1 - a));
	return R * c;
}

unsigned int Get_Trans(list<LINE *>* _line, unsigned int x) {
	for (list<LINE *>::iterator lit = _line->begin(); lit != _line->end(); lit++) {
		if ((*lit)->R_ID == x)
			return ((*lit)->maxDelay/2) * NomalRate;
	}
	return 0;
}

char *jbyteArray2cstr(JNIEnv *env, jbyteArray javaBytes) {
	size_t len = env->GetArrayLength(javaBytes);
	jbyte *nativeBytes = env->GetByteArrayElements(javaBytes, 0);
	char *nativeStr = (char *) malloc(len + 1);
	strncpy(nativeStr, (const char*) nativeBytes, len);
	nativeStr[len] = '\0';
	env->ReleaseByteArrayElements(javaBytes, nativeBytes, JNI_ABORT);
	return nativeStr;
}

jbyteArray cstr2jbyteArray(JNIEnv *env, const char *nativeStr) {
	jbyteArray javaBytes;
	int len = strlen(nativeStr);
	javaBytes = env->NewByteArray(len);
	env->SetByteArrayRegion(javaBytes, 0, len, (jbyte *) nativeStr);
	return javaBytes;
}

char *m_itoa(unsigned int _int) {
	char Temp[10] = { 0, };

	for (int i = 9; i >= 0; i--) {
		Temp[i] = _int % 10 + '0';
		_int = _int / 10;
	}
	return Temp;
}

int m_atoi(const char*str) {
	int re, i;
	re = 0;
	for (i = 0; str[i] != 0; ++i)
		re = 10 * re + str[i] - '0';

	return re;
}

extern "C" {
JNIEXPORT jobject JNICALL Java_com_example_findway_StartFinding_Algorithm(
		JNIEnv* pEnv, jclass pClass, jint StartLocationID, jint EndLocationID,
		jint SpendTime, jint DelayTime) {
//Main Ver
	//static char*			Start 		= jbyteArray2cstr(pEnv,StartLocationName);
	//static char*			Final 		= jbyteArray2cstr(pEnv,EndLocationName);
	static int End_Time = 0x0fffffff;
	static int trTime = 0;
	static int tiTime = -1;
	static int deTime = 0;
	static double Dist = 0;
	static list<STATE *> Next;		//STATE *형식으로 다음 갈길을 저장한다.
	static list<STATE *> End;		//Final Temp
	static list<INFO *> rInfo;		//모든 버스 정보를 가진다.
	static list<LINE *> lInfo;		//모든 노선의 배차시간을 가진다.
	static list<pair<double, double> > path;
	static list<INFO *>::iterator First;
	static list<INFO *>::iterator Second;
	static list<INFO *>::iterator Start;
	static list<INFO *>::iterator Final;
	static list<INFO *>::iterator Before;
	static list<STATE *>::iterator sit;
	static list<INFO *>::iterator it;
	static list<list<INFO *>::iterator>::iterator pit;
	//Sub Ver
	static STATE* Now = NULL;
	static STATE* New = NULL;
	static INFO * Load = NULL;
	static LINE * Line = NULL;
	static STATE* mkTemp = NULL;
	static pair<unsigned int, unsigned int> tPair;

	//DB 설정 공통 변수
	static FILE* _finfo;
	static FILE* _fsub;
	static char Buffer[1000] = { 0, };
	static char Tok[2] = { ' ', '^' };
	static char UP[3] = "정";
	//몰라
	static pair<double, double> tt;
	static char _gt[30] = { 0, };
	static const char* returndata;
	static int Tt;
	static int choose = 0;
	double a2;
	static int z = 0,o = 0;
//System Management
	// 먼저 tiTime은 static 변수로 -1 값을 가지므로 초기 else문으로 들어가 초기설정을 진행
	// 초기 설정 완료후 A type을 반환함으로써 GetTime요청
	// 이후 tiTime 무조건 0이상의 값을 가진다.

	Start:

	if (choose == 1) {
		tiTime = SpendTime * NomalRate;
	} else if (choose == 2) {
		deTime = DelayTime * NomalRate;
		goto _EC_;
	} else if (choose == 3) {
		goto _DY_;
	} else if (choose == 4){
		goto _DY1_;}
	else if (choose == 5){
		goto _DY2_;}
	else if( choose == 6){
		goto _DY6_;}



	if (tiTime >= 0) {	//최소경로 탐색
		Now = Next.front();
		if (Now->Time > End_Time) {
			cout << "End" << endl;
			goto End;
		}
		Next.pop_front();
		Before = Now->it;
		Before--;

		//Add temporary state
		//cout << "in? " << (*Before)->S_NM  << " " << (*Now->it)->S_NM << " " << Next.size() << " " << Next.front()->Time << endl;
		for (it = rInfo.begin(); it != rInfo.end(); it++) {
			//Search station
			if (!strcmp((*it)->S_NM, (*Before)->S_NM)) {

				if (Haversine_Distance((*it)->y, (*it)->x, (*Start)->y,
						(*Start)->x) < Dist) {
					if (Haversine_Distance((*it)->y, (*it)->x, (*Final)->y,
							(*Final)->x) < Dist) {

						New = new STATE;
						//Check transfer
						if ((*it)->R_ID == (*Now->it)->R_ID) {	//No
							//Copy Iterator
							New->it = it;
							//End - Copy iterator
							//Copy path
							for (pit = Now->Path.begin(); pit != Now->Path.end();
									pit++) {
								New->Path.push_back(*pit);
							}							//End - Copy path
							//Set Time
							New->Time = Now->Time + tiTime;
							//End - Set Time
						} else {								//Yes
							//Copy Iterator
							New->it = it;
							//End - Copy iterator
							//Get trTime 는 무조건 그차의 배차 시간과 같다.
							trTime = Get_Trans(&lInfo, (*Now->it)->R_ID);
							if (trTime == 0) {
								delete New;
								continue;
							}							//End - Get trTime
							//Set Time
							New->Time = Now->Time + tiTime + trTime;
							//End - Set Time
							//Copy path
							for (pit = Now->Path.begin();
									pit != Now->Path.end(); pit++) {
								New->Path.push_back(*pit);
							}							//End - Copy path
														//Add path
							New->Path.push_front(it);
							//End - Add path
						}								//End - Check transfer
														// Time Compare
						if ((*New->it)->Time < New->Time) {
							delete New;
							continue;
						}						//End Time Compare
												//Time Mark Update
						(*New->it)->Time = New->Time;
						//End - Time mark update
						//End Check
						if (!strcmp((*New->it)->S_NM, (*Start)->S_NM)) {
							 choose = 2;
							_gt[0] = 'B';
							 Tt = (*New->it)->R_ID;
							 _gt[9] = Tt % 10 +'0';
							 Tt = Tt / 10;
							 _gt[8] = Tt % 10 +'0';
							 Tt = Tt / 10;
							 _gt[7] = Tt % 10 +'0';
							 Tt = Tt / 10;
							 _gt[6] = Tt % 10 +'0';
							 Tt = Tt / 10;
							 _gt[5] = Tt % 10 +'0';
							 Tt = Tt / 10;
							 _gt[4] = Tt % 10 +'0';
							 Tt = Tt / 10;
							 _gt[3] = Tt % 10 +'0';
							 Tt = Tt / 10;
							 _gt[2] = Tt % 10 +'0';
							 Tt = Tt / 10;
							 _gt[1] = Tt % 10 +'0';

							 Tt = (*Before)->S_ID;
							_gt[18] = Tt % 10 + '0';
							Tt = Tt / 10;
							_gt[17] = Tt % 10 + '0';
							Tt = Tt / 10;
							_gt[16] = Tt % 10 + '0';
							Tt = Tt / 10;
							_gt[15] = Tt % 10 + '0';
							Tt = Tt / 10;
							_gt[14] = Tt % 10 + '0';
							Tt = Tt / 10;
							_gt[13] = Tt % 10 + '0';
							Tt = Tt / 10;
							_gt[12] = Tt % 10 + '0';
							Tt = Tt / 10;
							_gt[11] = Tt % 10 + '0';
							Tt = Tt / 10;
							_gt[10] = Tt % 10 + '0';

							_gt[19] = DelayTime / 10 + '0';
							_gt[20] = DelayTime % 10 + '0';
							_gt[21] = 0;
							 return pEnv->NewStringUTF(_gt);
	_EC_:


							//End - Get_Delay
							if(DelayTime == -1){
								delete New;
								continue;
							}

							New->Time += deTime;
							if (New->Time < End_Time) {
								End_Time = New->Time;
								while (End.size()) {
									STATE *zbcd = End.front();
									End.pop_front();

									delete zbcd;
								}
								End.push_back(New);
							}
							continue;
						}//End End Check
						//Add State
						for( sit = Next.begin() ; sit != Next.end() ; sit ++){
							if( (*sit)->Time <= New->Time){
							} else {
								break;
							}
						}
						Next.insert(sit, New);
						//End - Add state
						continue;
					}
				}
			}						//End - Search station
		}						//End - Add temporary state
		goto _GT_;
	} else {
		//초기설정 단계
		if (rInfo.empty()) { //Route 설정
			_finfo = fopen("/sdcard/Route.txt", "r");
			_fsub = fopen("/sdcard/Station.txt", "r");
			fgets(Buffer, 999, _finfo);
			while (!feof(_finfo)) {
				Load = new INFO;
				fgets(Buffer, 999, _finfo);
				Load->R_ID = atoi(strtok(Buffer, Tok));
				Load->S_ID = atoi(strtok(NULL, Tok));
				Load->UD = (strtok(NULL, Tok)[0] == UP[0]) ? 'U' : 'D';
				Load->Index = atoi(strtok(NULL, Tok));
				strcpy(Load->B_NM, strtok(NULL, Tok));
				strcpy(Load->S_NM, strtok(NULL, Tok));
				Load->Time = 0x0fffffff;
				fgets(Buffer, 999, _fsub);
				Load->x = atof(strtok(Buffer, " "));
				Load->y = atof(strtok(NULL, "^"));
				rInfo.push_back(Load);
			}
			fclose(_fsub);
			fclose(_finfo);
		} //End - Init Route Info
		if (lInfo.empty()) { //Line 설정
			_finfo = fopen("/sdcard/Line.txt", "r");
			fgets(Buffer, 999, _finfo);
			while (!feof(_finfo)) {
				Line = new LINE;
				fgets(Buffer, 999, _finfo);
				Line->R_ID = atoi(strtok(Buffer, "|"));
				strtok(NULL, ":");
				strtok(NULL, "|");
				strtok(NULL, "|");
				strtok(NULL, "|");
				strtok(NULL, "|");
				strtok(NULL, "|");
				Line->maxDelay = atoi(strtok(NULL, "|"));
				lInfo.push_back(Line);
			}
			fclose(_finfo);
		} //End - Init Line Info
		  //Init Temp


		for (list<INFO *>::iterator tit = rInfo.begin(); tit != rInfo.end(); tit++) {//이게 문제

			if (StartLocationID == (*tit)->S_ID) {
				z = 1;
				Start = tit;
			} else if (EndLocationID == (*tit)->S_ID){
				o = 1;
				Final = tit;
			}
		}
		if(z+o != 2){
			return pEnv->NewStringUTF("FF");
		}
		z = 0;
		o = 0;
		Dist = Haversine_Distance((*Final)->y, (*Final)->x, (*Start)->y, (*Start)->x);
		if(Dist < 0.7){
			return pEnv->NewStringUTF("GG");
		}
		Dist =  Dist * dRate;

		//End - Init Temp
		if (Next.empty()) { //Next 설정
			for (list<INFO *>::iterator it = rInfo.begin(); it != rInfo.end(); it++) {
				if (!strcmp((*it)->S_NM, (*Final)->S_NM)) {
					if (Haversine_Distance((*it)->y, (*it)->x, (*Start)->y,	(*Start)->x) < Dist) {
						if (Haversine_Distance((*it)->y, (*it)->x, (*Final)->y,	(*Final)->x) < Dist) {
							mkTemp = new STATE;
							mkTemp->Time = 0;
							mkTemp->it = it;
							mkTemp->Path.push_front(it);
							Next.push_back(mkTemp);
						}
					}
				}	//End - Add First State
			}		//End - Info Search
		}		//End - Init First
				//Get_Time
_GT_:
		if (Next.empty()) {
			return pEnv->NewStringUTF("HHError");
			goto _DY2_;
		}
		Now = Next.front();
		//Search before station
		Before = Now->it;
		--Before;
		//예외 추가

		if ((*Before)->R_ID != (*Now->it)->R_ID) {
			Next.pop_front();
			delete Now;
			goto _GT_;
		}
		if ((*Before)->UD != (*Now->it)->UD) {
			Next.pop_front();
			delete Now;
			goto _GT_;
		}
		//End - Search before station

		_gt[0] = 'A';
		Tt = (*Now->it)->R_ID;
		_gt[9] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[8] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[7] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[6] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[5] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[4] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[3] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[2] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[1] = Tt % 10 + '0';

		Tt = (*Before)->S_ID;
		_gt[18] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[17] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[16] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[15] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[14] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[13] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[12] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[11] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[10] = Tt % 10 + '0';

		Tt = (*Now->it)->S_ID;
		_gt[27] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[26] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[25] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[24] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[23] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[22] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[21] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[20] = Tt % 10 + '0';
		Tt = Tt / 10;
		_gt[19] = Tt % 10 + '0';

		//일반버스 광역버스 구별 부분
		double _dist;
		_dist = Haversine_Distance((*Now->it)->y, (*Now->it)->x, (*Before)->y, (*Before)->x);
		if (_dist < MaxDistance) {  	//일반버스
			tiTime = _time(_dist);
			choose = 0;
			goto Start;
		} else{
			//광역버스
			choose = 1;
			return pEnv->NewStringUTF("EASDASD");
			return pEnv->NewStringUTF(_gt);
		}
		//End - 일반버스 광역버스 구분
	}				//End - Main Loop

	End:
choose = 3;
_DY_:	//3 d 좌표
		New = new STATE;
		for(pit = End.front()->Path.begin(); pit != End.front()->Path.end(); pit ++){
			New->Path.push_back( *pit );
		}

		First = New->Path.back();
		New->Path.pop_back();

		if(New->Path.size() == 0){
			Second = Start;
		}
		else {
			Second = New->Path.back();
			New->Path.pop_back();
		}

		do{
			tt.first	= (*First)->x;
			tt.second	= (*First)->y;
			path.push_front(tt);

			First --;

			if( !strcmp( (*First)->S_NM, (*Second)->S_NM) ){
				if( New->Path.size()){
					First = Second;
					Second = New->Path.back();
					New->Path.pop_back();
				}
				else {
					if( strcmp( (*Second)->S_NM, (*Start)->S_NM) ){
						First = Second;
						Second = Start;
					}else{
						tt.first	= (*First)->x;
						tt.second	= (*First)->y;
						path.push_front(tt);
						break;
					}
				}
			}
		}while(1);


	strncpy(Buffer, Buffer+ 600, 300);
	Buffer[0] = 'D';

	for (list<pair<double, double> >::iterator it = path.begin();
			it != path.end(); it++) {
		a2 = it->first;
		sprintf(_gt, "%lf", a2);
		strcat(Buffer, _gt);
		strcat(Buffer, "|");

		a2 = it->second;
		sprintf(_gt, "%lf", a2);
		strcat(Buffer, _gt);
		strcat(Buffer, "|");
	}
	strcat(Buffer, "&");


	choose = 4;
	returndata = Buffer;
	return pEnv->NewStringUTF(returndata);

_DY1_:		// 4 c SID
	if(End.back()->Path.size() == 0)
		goto _DY2_;

	if( !strcmp( (*End.front()->Path.front())->S_NM , (*Start)->S_NM ) ){
		Tt = (*End.front()->Path.front())->S_ID;
		End.front()->Path.pop_front();
	} else {
		//위  _DY_ 의 결과 First 는 Start와 같은 이름의 정류소를 가리키는 노선의 이터레이터이다
		Tt = (*First)->S_ID;
	}

	_gt[0] = 'C';
	_gt[9] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[8] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[7] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[6] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[5] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[4] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[3] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[2] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[1] = Tt % 10 + '0';
	_gt[10] ='|';
	_gt[11] = 0;

	choose = 6;
	strcat(_gt,  (*End.front()->Path.front())->B_NM);
	return pEnv->NewStringUTF(_gt);

_DY6_:

	Tt = (*End.front()->Path.front())->S_ID;
	End.front()->Path.pop_front();

	_gt[0] = 'C';
	_gt[9] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[8] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[7] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[6] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[5] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[4] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[3] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[2] = Tt % 10 + '0';
	Tt = Tt / 10;
	_gt[1] = Tt % 10 + '0';
	_gt[10] ='|';
	_gt[11] = 0;
	if(End.front()->Path.size() == 0){
		choose = 5;
		strcat(_gt, "&");
	} else {
		strcat(_gt,  (*End.front()->Path.front())->B_NM);
	}
	return pEnv->NewStringUTF(_gt);


	_DY2_: //여기는 무조건 Memory Free를 가리켜야 함
	////////////////////////////////////////
	//Memory Free
	//Next list 해체
	while (!Next.empty()) {
		Now = Next.front();
		Next.pop_front();
		delete Now;
	}
	//Info list 해체
	while (!rInfo.empty()) {
		Load = rInfo.front();
		rInfo.pop_front();
		delete Load;
	}
	//line list 해체
	while (!lInfo.empty()) {
		Line = lInfo.front();
		lInfo.pop_front();
		delete Line;
	}
	path.clear();
	_gt[0] = 'E';
	End_Time /= NomalRate;
	_gt[1] = End_Time / 10 + '0';
	_gt[2] = End_Time % 10 + '0';
	_gt[3] = 0;
	//memset(Buffer, 1000, 0);
	tiTime = -1;
	End_Time = 0x0fffffff;
	trTime = 0;
	deTime = 0;
	Dist = 0;
	choose = 0;
	Now = NULL;
	New = NULL;
	Load = NULL;
	Line = NULL;
	mkTemp = NULL;

	return pEnv->NewStringUTF(_gt);

}
}

